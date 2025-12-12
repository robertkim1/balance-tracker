import { createClient } from "@supabase/supabase-js";

class NoStorage {
  getItem(key: string) { 
    void key;
    return null; 
  }
  setItem(key: string, value: string) {
    void key;
    void value;
  }
  removeItem(key: string) {
    void key;
  }
}

const supabaseUrl = process.env.NEXT_PUBLIC_SUPABASE_URL as string;
const supabaseAnonKey = process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY as string;

export const supabase = createClient(supabaseUrl, supabaseAnonKey, {
  auth: {
    storage: new NoStorage(),        // prevents localStorage usage
    persistSession: false,           // no client-side persistence
    autoRefreshToken: false,         // server handles refresh
    detectSessionInUrl: false,       // no URL token auto-detection
  }
});
