package ua.roxgames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Class for the frame and panel creation, adding pieces to its grid as well as swapping of the pieces
 * and checking the result
 * @author Roxy
 *
 */
public class Puzzle extends JFrame {
    private JPanel panel;
    private List<Piece> pieces;

    private final int FRAME_WIDTH = 500;
    private final int WIDTH_NUM = 4; // number of grid elements on a panel by width
    private final int HEIGHT_NUM = 4; // number of grid elements on a panel by height
    private final int GRID_SIZE = WIDTH_NUM * HEIGHT_NUM;

    private final String IMAGE_FILENAME = "src/ua/roxgames/rose-heart-picture.jpg";

    public Puzzle() {
        makeGUI();
        init();
    }

    /**
     * Create a GUI on the frame
     */
    private void makeGUI() {
        panel = new JPanel();
        panel.setLayout(new GridLayout(HEIGHT_NUM, WIDTH_NUM, 0, 0));

        add(panel, BorderLayout.CENTER); // add panel to the frame

        pack(); // panel is sized to fit frame

        setTitle("Puzzle Game");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Cut image and add shuffled pieces and an empty piece to the panel 
     */
    private void init() {
        pieces = new ArrayList<>();

        BufferedImage resizedImage = null;

        try {
            resizedImage = getResizedImage(IMAGE_FILENAME); // change image resolution
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Cannot load image", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        int width = resizedImage.getWidth(null);
        int height = resizedImage.getHeight(null);

        int pieceIndex = 1;
        for (int i = 0; i < HEIGHT_NUM; i++) {  // make a piece by height and width
            for (int j = 0; j < WIDTH_NUM; j++) {
                Image image = createImage(new FilteredImageSource(resizedImage.getSource(),
                        new CropImageFilter(j * width / WIDTH_NUM, i * height / HEIGHT_NUM, width / WIDTH_NUM, height / HEIGHT_NUM)));
                
                // unless it is a last piece we create our pieces from a picture
                if (pieceIndex != GRID_SIZE) {
                    Piece piece = new Piece(image, pieceIndex);
                    piece.setBorder(BorderFactory.createLineBorder(Color.blue));
                    piece.addActionListener(new Click());
                    pieces.add(piece);
                    pieceIndex++;      
                }
            }
        }

        Collections.shuffle(pieces);
        
        Piece emptyPiece = new Piece(pieceIndex);
        emptyPiece.setContentAreaFilled(false); // makes a piece look empty
        pieces.add(emptyPiece);

        for (Piece piece : pieces) {
            panel.add(piece);
        }

        pack(); // all elements are sized to fit frame
    }

    /**
     * Resize an image to fit the panel
     * @param filename
     * @return resized image
     * @throws IOException
     */
    private BufferedImage getResizedImage(String filename) throws IOException {
        BufferedImage source = loadImage(IMAGE_FILENAME);
        int desiredHeight = getDesiredHeight(source.getWidth(), source.getHeight());
        return resizeImage(source, FRAME_WIDTH, desiredHeight, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Load image from the file
     * @param filename
     * @return an image
     * @throws IOException
     */
    private BufferedImage loadImage(String filename) throws IOException {
        return ImageIO.read(new File(filename));
    }

    /**
     * Based on image width we get a desired image height by calculating width to height ratio
     * @param image width
     * @param image height
     * @return
     */
    private int getDesiredHeight(int width, int height) {
        double ratio = FRAME_WIDTH / (double) width;
        return (int) (height * ratio); // calculate width to height ratio
    }

    /**
     * Change image size and draw image to the panel
     * @param originalImage
     * @param width
     * @param height
     * @param type
     * @return resized image
     * @throws IOException
     */
    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height, int type) throws IOException {
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose(); // close Graphics context and show resources

        return resizedImage;
    }

    /**
     * Inner class for a click and swap actions for the image pieces on the panel
     * @author Roxy
     *
     */
    private class Click extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent event) {
            checkPiece(event);
            checkResult();    // check picture - finished already?
        }

        /**
         * Check if a piece can be moved and move it by clicking
         * @param e - click event
         */
        private void checkPiece(ActionEvent e) {
            int emptyPieceIndex = 0;

            for (Piece piece : pieces) {
                if (piece.isEmptyPiece()) {
                    emptyPieceIndex = pieces.indexOf(piece);
                }
            }

            Piece piece = (Piece) e.getSource(); // element, which was clicked
            int clickedPieceIndex = pieces.indexOf(piece);

            boolean swap = false;
            if (clickedPieceIndex / WIDTH_NUM == emptyPieceIndex / WIDTH_NUM) { // if an empty and clicked piece are on the same row  
                if ((clickedPieceIndex - 1 == emptyPieceIndex) || (clickedPieceIndex + 1 == emptyPieceIndex)) {
                    swap = true;
                }
            } else {
                if ((clickedPieceIndex - WIDTH_NUM == emptyPieceIndex) || (clickedPieceIndex + WIDTH_NUM == emptyPieceIndex)) {
                    swap = true;
                }
            }

            if (swap) {
                Collections.swap(pieces, clickedPieceIndex, emptyPieceIndex);
                updatePieces();
            }
        }

        /**
         * Repaint pieces after swap
         */
        private void updatePieces() {
            panel.removeAll();

            for (Piece piece : pieces) {
                panel.add(piece);
            }

            panel.validate(); // do repainting
        }
    }

    /**
     * Verify that a final result is the same as an image by checking each piece index
     */
    private void checkResult() {
        boolean result = true;

        for (int i = 1; i <= GRID_SIZE; i++) {
            if (pieces.get(i).getIndex() != i) { // check if piece index is not equal new piece index
                result = false;
                break;
            }
        }

        if (result) {
            JOptionPane.showMessageDialog(panel, "Done!","Congratulations", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new Puzzle();
    }
}