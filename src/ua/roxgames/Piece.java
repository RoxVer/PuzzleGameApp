package ua.roxgames;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * Class for a single piece of a puzzle
 * @author Roxy
 *
 */
public class Piece extends JButton {
    private boolean emptyPiece = false;
    private int index = 0;

    public Piece(int index) {
        this.index = index;
        emptyPiece = true;
    }

    public Piece(Image image, int index) {
        super(new ImageIcon(image));
        this.index = index;
    }

    public boolean isEmptyPiece() {
        return emptyPiece;
    }

    public int getIndex() {
        return index;
    }
}
