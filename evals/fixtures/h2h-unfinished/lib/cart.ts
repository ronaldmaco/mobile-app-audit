import AsyncStorage from '@react-native-async-storage/async-storage';

export type CartLine = { productId: string; name: string; price: number; qty: number };
const CART_KEY = 'cart';

export async function readCart(): Promise<CartLine[]> {
  const raw = await AsyncStorage.getItem(CART_KEY);
  return raw ? JSON.parse(raw) : [];
}

export async function addToCart(line: CartLine): Promise<void> {
  const cart = await readCart();
  const existing = cart.find((l) => l.productId === line.productId);
  if (existing) existing.qty += line.qty;
  else cart.push(line);
  await AsyncStorage.setItem(CART_KEY, JSON.stringify(cart));
}

export function total(cart: CartLine[]): number {
  return cart.reduce((sum, l) => sum + l.price * l.qty, 0);
}
