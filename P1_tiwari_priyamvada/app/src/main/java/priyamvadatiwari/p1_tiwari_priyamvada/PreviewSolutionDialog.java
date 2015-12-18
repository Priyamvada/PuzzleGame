package priyamvadatiwari.p1_tiwari_priyamvada;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * @author priyamvadatiwari
 * Created by priyamvadatiwari on 12/18/15.
 * This class covers one of the bells and whistles namely where you can view the solution
 * image of a puzzle
 *
 * ChangeImageDialog launches a customized Dialog with image tiles to choose from.
 * The custom markup is read from solution_prompt_dialog.xml
 *
 * @property mLayout - the inflater layout which is contained in the dialog
 * @property mImageView - the ImageLayout on which the current image is to be painted as the solution
 * @property mGameImage - the current Game Image
 */
public class PreviewSolutionDialog  extends DialogFragment {

    private View mLayout;
    private ImageView mImageView;
    private Bitmap mGameImage;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The Builder class is used here for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mLayout = inflater.inflate(R.layout.solution_prompt_dialog, null);

        mImageView = (ImageView) mLayout.findViewById(R.id.prompt_image);
        mGameImage = ((MainActivity) this.getActivity()).gameBmp;
        this.setLayoutBitmap(mGameImage);

        final PreviewSolutionDialog that = this;

        builder.setView(mLayout);

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog d = (AlertDialog) getDialog();

        //Disabling the default ok and cancel buttons in the dialog
        if (d != null) {
            Button positiveButton = (Button)d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setEnabled(false);
            Button negativeButton = (Button)d.getButton(Dialog.BUTTON_NEGATIVE);
            negativeButton.setEnabled(false);
        }

    }

    private void setLayoutBitmap(Bitmap gameBmp)  {
        if (mImageView != null) {
            mImageView.setImageBitmap(gameBmp);
        }
    }

    public void show(FragmentManager fragmentManager, String tag, Bitmap gameBmp)  {
        super.show(fragmentManager, tag);
        this.setLayoutBitmap(gameBmp);
    }
}
