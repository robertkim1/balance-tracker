"use client";

import { createContext, useContext, useState, ReactNode, useEffect } from "react";
import { supabase } from "./supabase";
import { Session } from "@supabase/supabase-js";

type AuthContextType = {
  jwt: string | null;
  session: Session | null;
  signInWithGoogle: () => Promise<void>;
  signOut: () => Promise<void>;
  isLoading: boolean;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [jwt, setJwt] = useState<string | null>(null);
  const [session, setSession] = useState<Session | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  // Initialize session on mount
  useEffect(() => {
    const initializeAuth = async () => {
      const { data: { session } } = await supabase.auth.getSession();
      
      if (session) {
        setSession(session);
        setJwt(session.access_token);
      }
      
      setIsLoading(false);
    };

    initializeAuth();

    // Listen for auth changes
    const { data: { subscription } } = supabase.auth.onAuthStateChange(
      async (_event, session) => {
        setSession(session);
        setJwt(session?.access_token ?? null);
      }
    );

    return () => {
      subscription.unsubscribe();
    };
  }, []);

  const signInWithGoogle = async () => {
    const { error } = await supabase.auth.signInWithOAuth({
      provider: "google",
      options: {
        redirectTo: process.env.NEXT_PUBLIC_FRONTEND_URL_DEV + "/auth/callback"
      }
    });

    if (error) {
      console.error("Sign in error:", error);
      throw error;
    }
  };

  const signOut = async () => {
    const { error } = await supabase.auth.signOut();
    
    if (error) {
      console.error("Sign out error:", error);
      throw error;
    }

    // State will be cleared automatically by onAuthStateChange listener
  };

  return (
    <AuthContext.Provider value={{ jwt, session, signInWithGoogle, signOut, isLoading }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth must be used inside AuthProvider");
  }
  return ctx;
}