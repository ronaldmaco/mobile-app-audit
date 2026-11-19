import { Text } from 'react-native';

export function PriceTag({ value }: { value: number }) {
  return <Text style={{ fontWeight: '600' }}>${value.toFixed(2)}</Text>;
}
