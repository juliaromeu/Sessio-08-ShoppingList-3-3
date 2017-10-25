package edu.upc.eseiaat.pma.shoppinglist3;

/**
 * Created by juliaromeu on 25/10/17.
 */

public class ShoppingItem {

    private  String text;
    private boolean checked;

    //GENERAR CONSTRUCTRES
    // ctrl+enter -> Constructor --> text:String
    public ShoppingItem(String text) {
        this.text = text;
        this.checked = false;
    }
    // ctrl+enter -> Constructor --> text:String + checked:boolean
    public ShoppingItem(String text, boolean checked) {
        this.text = text;
        this.checked = checked;
    }

    // ctrl+enter -> Getter and Setter
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void toggleChecked() {
        //El inverso de lo que había,si estaba a TRUE pasa a FALSE
        this.checked = !this.checked;
    }
}
