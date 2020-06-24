/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 14.06.20, 16:38
 */

package net.bplaced.abzzezz.animeapp.activities.main.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import id.ionbit.ionalert.IonAlert;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.activities.main.SelectedAnimeActivity;
import net.bplaced.abzzezz.animeapp.util.ImageUtil;
import net.bplaced.abzzezz.animeapp.util.scripter.DataBaseSearch;
import net.bplaced.abzzezz.animeapp.util.scripter.URLHandler;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class AnimeListFragment extends Fragment {

    private AnimeAdapter animeAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.anime_list_layout, container, false);

        GridView gridView = root.findViewById(R.id.anime_grid);
        this.animeAdapter = new AnimeAdapter(AnimeAppMain.getInstance().getAnimeSaver().getList(), getActivity().getApplicationContext());
        gridView.setAdapter(animeAdapter);
        /**
         * Set onclick listener, if clicked pass information through to selected anime.
         */
        gridView.setOnItemClickListener((parent, view, position, id) -> new Handler().postDelayed(() -> {
            try {
                Intent intent = new Intent(getActivity(), SelectedAnimeActivity.class);
                String[] pass = AnimeAppMain.getInstance().getAnimeSaver().getAll(position);
                String[] dataBase = new DataBaseSearch().execute(pass[3]).get();
                intent.putExtra("anime_name", pass[0]);
                intent.putExtra("anime_episodes", dataBase[1]);
                intent.putExtra("anime_cover", pass[2]);
                intent.putExtra("anime_aid", pass[3]);
                intent.putExtra("anime_language", dataBase[4]);
                startActivity(intent);
                getActivity().finish();

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }, 100));
        /**
         * Set on long hold listener, then block the old one
         */
        gridView.setOnItemLongClickListener((parent, view, position, id) -> {
            new IonAlert(getActivity(), IonAlert.WARNING_TYPE)
                    .setTitleText("Remove file?")
                    .setContentText("Won't be able to recover this file!")
                    .setConfirmText("Yes,delete it!")
                    .setConfirmClickListener(ionAlert -> {
                        animeAdapter.removeItem(position);
                        ionAlert.dismissWithAnimation();
                    }).setCancelText("Abort").setCancelClickListener(IonAlert::dismissWithAnimation)
                    .show();
            return true;
        });

        FloatingActionButton addAidButton = root.findViewById(R.id.add_aid);
        addAidButton.setOnClickListener(v -> {
            EditText editText = new EditText(getActivity());
            new AlertDialog.Builder(getActivity()).setTitle("Aid").setMessage("Enter Aid to add").setPositiveButton("Enter", (dialogInterface, i) -> {
                animeAdapter.addItem(editText.getText().toString());
            }).setView(editText).show();
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }


    /**
     * @Override public boolean onOptionsItemSelected(MenuItem item) {
     * int itemID = item.getItemId();
     * switch (itemID) {
     * case R.id.add_aid_item:
     * InputDialog input = new InputDialog("Aid ");
     * input.show(getSupportFragmentManager(), "Enter AID");
     * break;
     * case R.id.add_series_cloud:
     * Intent intent = new Intent(this, CloudList.class);
     * startActivity(intent);
     * break;
     * case R.id.menu_options:
     * Intent intent1 = new Intent(this, SettingsActivity.class);
     * startActivity(intent1);
     * break;
     * case R.id.cloud_save:
     * new FTPGetter().execute();
     * break;
     * default:
     * break;
     * }
     * return super.onOptionsItemSelected(item);
     * }
     * <p>
     * /**
     * Array Picture adapter
     */


    private class AnimeAdapter extends BaseAdapter {

        private final Context context;
        private final List<String> string;

        public AnimeAdapter(List<String> string, Context context) {
            this.string = string;
            this.context = context;
        }

        @Override
        public int getCount() {
            return string.size();
        }

        @Override
        public Object getItem(int position) {
            return string.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(context);
            try {
                Picasso.with(context).load(AnimeAppMain.getInstance().getAnimeSaver().getAll(position)[2]).resize(ImageUtil.dimensions[0], ImageUtil.dimensions[1]).into(imageView);
                imageView.setAdjustViewBounds(true);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println(position);
            }
            return imageView;
        }

        public void removeItem(int index) {
            string.remove(index);
            AnimeAppMain.getInstance().getAnimeSaver().remove(index);
            notifyDataSetChanged();
        }

        public void addItem(String item) {
            try {
                if (!URLHandler.isOnline(getActivity())) {
                    Toast.makeText(context, "You are currently not connected to the internet, returning", Toast.LENGTH_LONG).show();
                    return;
                }

                String[] all = new DataBaseSearch().execute(item).get();
                string.add(item);
                AnimeAppMain.getInstance().getAnimeSaver().add(all);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            notifyDataSetChanged();
        }

        public List<String> getString() {
            return string;
        }
    }

    /**
     * Debug
     */
    /*
    class FTPGetter extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                FTPClient ftpClient = new FTPClient();
                ftpClient.connect("abzzezz.bplaced.net");
                ftpClient.login("abzzezz_client", "AzA33EUSgU7KZvbj");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                for (String line : SplashScreen.saver.getList()) {
                    baos.write((line + "\n").getBytes());
                }
                byte[] bytes = baos.toByteArray();
                Logger.log("File uploaded: " + ftpClient.storeFile("/www/lists/" + new Random().nextInt() + ".txt", new ByteArrayInputStream(bytes)), Logger.LogType.INFO);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
     */
}
