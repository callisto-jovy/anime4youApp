package net.bplaced.abzzezz.animeapp.activities.main.ui.tracker;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import net.bplaced.abzzezz.animeapp.R;

public class DownloadTrackerFragmentDirections {
  private DownloadTrackerFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionDownloadTrackerToNavChangelog() {
    return new ActionOnlyNavDirections(R.id.action_download_tracker_to_nav_changelog);
  }
}
