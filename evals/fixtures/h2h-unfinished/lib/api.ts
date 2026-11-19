import { API_URL } from './config';
import { getToken } from './auth';

export type Product = { id: string; name: string; price: number; stock: number; image: string };

async function get<T>(path: string): Promise<T> {
  const token = await getToken();
  const res = await fetch(`${API_URL}${path}`, {
    headers: token ? { Authorization: `Bearer ${token}` } : {},
  });
  return res.json();
}

export async function listProducts(): Promise<Product[]> {
  try {
    return await get<Product[]>('/products');
  } catch {
    return [];
  }
}

export async function getProduct(id: string): Promise<Product | null> {
  try {
    return await get<Product>(`/products/${id}`);
  } catch {
    return null;
  }
}
