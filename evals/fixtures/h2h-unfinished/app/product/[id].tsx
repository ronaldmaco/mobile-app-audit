import { useEffect, useState } from 'react';
import { Button, Image, Text, View } from 'react-native';
import { useLocalSearchParams } from 'expo-router';
import { getProduct, Product } from '../../lib/api';
import { addToCart } from '../../lib/cart';
import { PriceTag } from '../../components/PriceTag';

export default function ProductDetail() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const [product, setProduct] = useState<Product | null>(null);

  useEffect(() => {
    getProduct(id).then(setProduct);
  }, [id]);

  if (!product) return <Text style={{ padding: 24 }}>Loading...</Text>;

  // TODO: handle out of stock (disable button, show badge)
  return (
    <View style={{ padding: 24, gap: 12 }}>
      <Image source={{ uri: product.image }} style={{ width: '100%', height: 240 }} />
      <Text style={{ fontSize: 22 }}>{product.name}</Text>
      <PriceTag value={product.price} />
      <Button
        title="Add to cart"
        onPress={() => addToCart({ productId: product.id, name: product.name, price: product.price, qty: 1 })}
      />
    </View>
  );
}
