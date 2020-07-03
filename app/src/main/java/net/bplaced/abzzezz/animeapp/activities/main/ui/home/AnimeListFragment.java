/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.06.20, 15:16
 */

package net.bplaced.abzzezz.animeapp.activities.main.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import id.ionbit.ionalert.IonAlert;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.activities.main.SelectedAnimeActivity;
import net.bplaced.abzzezz.animeapp.util.ImageUtil;
import net.bplaced.abzzezz.animeapp.util.file.OfflineImageLoader;
import net.bplaced.abzzezz.animeapp.util.scripter.DataBaseSearch;
import net.bplaced.abzzezz.animeapp.util.scripter.URLHandler;
import net.bplaced.abzzezz.animeapp.util.tasks.DataBaseTask;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;

import java.util.List;

public class AnimeListFragment extends Fragment {

    private final DataBaseSearch dataBaseSearch = new DataBaseSearch();
    private AnimeAdapter animeAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.anime_list_layout, container, false);
        GridView gridView = root.findViewById(R.id.anime_grid);
        this.animeAdapter = new AnimeAdapter(AnimeAppMain.getInstance().getAnimeSaver().getList(), getActivity());
        gridView.setAdapter(animeAdapter);
        /**
         * Set onclick listener, if clicked pass information through to selected anime.
         */
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getActivity(), SelectedAnimeActivity.class);
            String[] pass = AnimeAppMain.getInstance().getAnimeSaver().getAll(position);
            new TaskExecutor().executeAsync(new DataBaseTask(pass[3], dataBaseSearch), new TaskExecutor.Callback<String[]>() {
                @Override
                public void onComplete(String[] result) {
                    String[] extra = result;

                    if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("offline_mode", false)) {
                        extra = pass;
                    }

                    intent.putExtra("anime_name", extra[0]);
                    intent.putExtra("anime_episodes", extra[1]);
                    intent.putExtra("anime_cover", extra[2]);
                    intent.putExtra("anime_aid", extra[3]);
                    intent.putExtra("anime_language", result[4]);

                    startActivity(intent);
                    getActivity().finish();
                }

                @Override
                public void preExecute() {
                }
            });
        });
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
            new AlertDialog.Builder(getActivity()).setTitle("Aid").setMessage("Enter Aid to add").setPositiveButton("Enter", (dialogInterface, i) -> animeAdapter.addItem(editText.getText().toString())).setView(editText).show();
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


    class AnimeAdapter extends BaseAdapter {

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
            String imageURL = AnimeAppMain.getInstance().getAnimeSaver().getAll(position)[2];
            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("offline_mode", false)) {
                OfflineImageLoader.loadImage(imageURL, AnimeAppMain.getInstance().getAnimeSaver().getAll(position)[3], imageView, getContext());
            } else {
                try {
                    Picasso.with(context).load(imageURL).resize(ImageUtil.dimensions[0], ImageUtil.dimensions[1]).into(imageView);
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println(position);
                }
            }
            imageView.setAdjustViewBounds(true);
            return imageView;
        }

        public void removeItem(int index) {
            string.remove(index);
            AnimeAppMain.getInstance().getAnimeSaver().remove(index);
            notifyDataSetChanged();
        }

        public void addItem(String item) {
            if (!URLHandler.isOnline(getActivity())) {
                Toast.makeText(context, "You are currently not connected to the internet, returning", Toast.LENGTH_LONG).show();
                return;
            }

            new TaskExecutor().executeAsync(new DataBaseTask(item, dataBaseSearch), new TaskExecutor.Callback<String[]>() {
                @Override
                public void onComplete(String[] result) {
                    string.add(item);
                    AnimeAppMain.getInstance().getAnimeSaver().add(result);
                    notifyDataSetChanged();
                }

                @Override
                public void preExecute() {
                }
            });
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
