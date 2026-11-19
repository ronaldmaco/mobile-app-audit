import { useFocusEffect } from 'expo-router';
import { useCallback, useState } from 'react';
import { Button, FlatList, Text, View } from 'react-native';
import { CartLine, readCart, total } from '../../lib/cart';
import { PriceTag } from '../../components/PriceTag';

export default function Cart() {
  const [lines, setLines] = useState<CartLine[]>([]);

  useFocusEffect(
    useCallback(() => {
      readCart().then(setLines);
    }, []),
  );

  return (
    <View style={{ flex: 1 }}>
      <FlatList
        data={lines}
        keyExtractor={(l) => l.productId}
        ListEmptyComponent={<Text style={{ padding: 24 }}>Your cart is empty.</Text>}
        renderItem={({ item }) => (
          <View style={{ flexDirection: 'row', justifyContent: 'space-between', padding: 12 }}>
            <Text>
              {item.qty} x {item.name}
            </Text>
            <PriceTag value={item.price * item.qty} />
          </View>
        )}
      />
      <View style={{ padding: 16, gap: 8 }}>
        <Text>
          Total: <PriceTag value={total(lines)} />
        </Text>
        <Button title="Checkout" onPress={() => {}} />
      </View>
    </View>
  );
}
