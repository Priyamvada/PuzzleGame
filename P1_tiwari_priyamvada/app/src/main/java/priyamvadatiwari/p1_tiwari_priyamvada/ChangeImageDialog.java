package priyamvadatiwari.p1_tiwari_priyamvada;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

/**
 * @author priyamvadatiwari
 * Created by priyamvadatiwari on 12/18/15.
 * This class covers one of the bells and whistles namely where you can change the image from
 * a given set of images
 *
 * ChangeImageDialog launches a customized Dialog with image tiles to choose from.
 * The custom markup is read from change_image_popup.xml
 *
 * @property mLayout - the inflater layout which is contained in the dialog
 * @property mIconGrid - the GridLayout comprising of all the image options to play the puzzle with
 * @property mImageIndex - the currently highlighted image in the dialog, ready to be selected
 */
public class ChangeImageDialog extends DialogFragment {

    private View mLayout;
    private GridLayout mIconGrid;
    private int mImageIndex = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The Builder class is used here for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mLayout = inflater.inflate(R.layout.change_image_popup, null);

        mIconGrid = (GridLayout) mLayout.findViewById(R.id.change_img_grid);

        this.attachListeners();
        final ChangeImageDialog that = this;

        builder.setView(mLayout)
                .setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (mImageIndex != 0) {
                            ((MainActivity) that.getActivity()).setGameImageIndex(mImageIndex);
                            ((MainActivity) that.getActivity()).newGame();
                        }
                        mImageIndex = 0;
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mImageIndex = 0;
                    }
                })
        ;

        return builder.create();
    }

    /**
     * @return the mIconGrid comprizing of tiles of image options
     */
    public GridLayout getIconGrid() {
        return this.mIconGrid;
    }

    /**
     * attaches listeners to listen to touch events on the image option tiles
     */
    public void attachListeners()   {
        final ChangeImageDialog that = this;

        for(int i =0; i < mIconGrid.getRowCount() * mIconGrid.getColumnCount(); i++)  {
            final ImageView icon = (ImageView) mIconGrid.getChildAt(i);
            icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    mImageIndex = mIconGrid.indexOfChild(icon) + 1;
                    ((MainActivity)that.getActivity()).clearFocusedTiles(mIconGrid, Color.TRANSPARENT);
                    icon.setBackgroundColor(Color.BLACK);
                }
            });

        }
    }
}
