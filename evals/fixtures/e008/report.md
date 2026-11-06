User message: "Saving my profile doesn't work, I tried yesterday." No video, no device, no app version.

Code: EditProfileViewController.save() disables the button, sends PATCH /profile with the edited fields,
on success updates the session user and closes; on failure shows the server error and re-enables the button.
API probe with a test account: PATCH with the same body returns 200 and GET /profile shows the new values.
