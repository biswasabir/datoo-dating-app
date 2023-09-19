package com.angopapo.datooapp.modules.instagram;

import com.angopapo.datooapp.models.others.InstagramUser;

public interface AuthCallback {
  void onSuccess(InstagramUser socialUser);

  void onError(Throwable error);

  void onCancel();
}
