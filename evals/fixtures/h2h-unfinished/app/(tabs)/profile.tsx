import { useState } from 'react';
import { Button, Image, View } from 'react-native';
import * as ImagePicker from 'expo-image-picker';
import { router } from 'expo-router';
import { logout } from '../../lib/auth';

export default function Profile() {
  const [avatar, setAvatar] = useState<string | null>(null);

  async function takePhoto() {
    const result = await ImagePicker.launchCameraAsync({ quality: 0.7 });
    if (!result.canceled) setAvatar(result.assets[0].uri);
  }

  async function signOut() {
    await logout();
    router.replace('/login');
  }

  return (
    <View style={{ padding: 24, gap: 12, alignItems: 'center' }}>
      {avatar && <Image source={{ uri: avatar }} style={{ width: 96, height: 96, borderRadius: 48 }} />}
      <Button title="Take profile photo" onPress={takePhoto} />
      <Button title="Sign out" onPress={signOut} />
    </View>
  );
}
