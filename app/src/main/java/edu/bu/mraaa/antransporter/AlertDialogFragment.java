package edu.bu.mraaa.antransporter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by mraaa711128 on 12/13/14.
 */
public class AlertDialogFragment extends DialogFragment {

    public static AlertDialogFragment newDialog(String title, String message) {
        AlertDialogFragment altDialogFrag = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("title",title);
        args.putString("message",message);
        altDialogFrag.setArguments(args);
        return altDialogFrag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setTitle(getArguments().getString("title"));
        alertDialog.setMessage(getArguments().getString("message"));
        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        //return super.onCreateDialog(savedInstanceState);
        return alertDialog.create();
    }
}
