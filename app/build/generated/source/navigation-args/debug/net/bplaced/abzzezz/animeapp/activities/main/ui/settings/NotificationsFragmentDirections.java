package net.bplaced.abzzezz.animeapp.activities.main.ui.settings;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import net.bplaced.abzzezz.animeapp.R;

public class NotificationsFragmentDirections {
  private NotificationsFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionDownloadTrackerToNavChangelog() {
    return new ActionOnlyNavDirections(R.id.action_download_tracker_to_nav_changelog);
  }

  @NonNull
  public static NavDirections actionAnimeNotificationSelf() {
    return new ActionOnlyNavDirections(R.id.action_anime_notification_self);
  }
}
