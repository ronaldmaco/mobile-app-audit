import { useState } from 'react';
import { Button, Text, TextInput, View } from 'react-native';
import { router } from 'expo-router';
import { login } from '../lib/auth';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);

  async function submit() {
    setError(null);
    const ok = await login(email, password);
    if (ok) router.replace('/(tabs)');
    else setError('Wrong email or password');
  }

  return (
    <View style={{ padding: 24, gap: 12 }}>
      <TextInput placeholder="Email" autoCapitalize="none" keyboardType="email-address" value={email} onChangeText={setEmail} />
      <TextInput placeholder="Password" secureTextEntry value={password} onChangeText={setPassword} />
      {error && <Text style={{ color: 'red' }}>{error}</Text>}
      <Button title="Sign in" onPress={submit} />
      <Button title="Forgot password?" onPress={() => router.push('/forgot-password')} />
    </View>
  );
}
