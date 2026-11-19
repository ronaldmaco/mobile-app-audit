import { useEffect, useState } from 'react';
import { FlatList, Image, Pressable, Text, View } from 'react-native';
import { router } from 'expo-router';
import { listProducts, Product } from '../../lib/api';
import { PriceTag } from '../../components/PriceTag';

export default function Shop() {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    listProducts().then((p) => {
      setProducts(p);
      setLoading(false);
    });
  }, []);

  if (loading) return <Text style={{ padding: 24 }}>Loading...</Text>;

  return (
    <FlatList
      data={products}
      keyExtractor={(p) => p.id}
      ListEmptyComponent={<Text style={{ padding: 24 }}>No plants available right now.</Text>}
      renderItem={({ item }) => (
        <Pressable onPress={() => router.push(`/product/${item.id}`)}>
          <View style={{ flexDirection: 'row', padding: 12, gap: 12 }}>
            <Image source={{ uri: item.image }} style={{ width: 64, height: 64 }} />
            <View>
              <Text>{item.name}</Text>
              <PriceTag value={item.price} />
            </View>
          </View>
        </Pressable>
      )}
    />
  );
}
