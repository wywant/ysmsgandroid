package wywant.msgandroid;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;


public class FireMissilesDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View contentView = inflater.inflate(R.layout.dialog_hostport, null);
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String hostname = sharedPref.getString("hostname", "");
        String port = sharedPref.getString("port","8080");
       final EditText hostEditText = (EditText)contentView.findViewById(R.id.host);
       final EditText portEditText  = (EditText)contentView.findViewById(R.id.port);
        hostEditText.setText(hostname);
        portEditText.setText(port);
        builder.setView(contentView)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //http://stackoverflow.com/questions/21396358/sharedpreferences-putstringset-doesnt-work
                        String hostname = hostEditText.getText().toString().trim();
                        String port = portEditText.getText().toString().trim();

                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.clear();
                        editor.putString("hostname",hostname);
                        editor.putString("port",port);
                        editor.commit();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //LoginDialogFragment.this.getDialog().cancel();
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
