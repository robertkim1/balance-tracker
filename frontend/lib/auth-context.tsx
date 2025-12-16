"use client";

import {
  createContext,
  useContext,
  useEffect,
  useState,
  ReactNode
} from "react";

import { supabase } from "./supabase";

type AuthContextType = {
  isAuthenticated: boolean;
  isLoading: boolean;
  signInWithGoogle: () => Promise<void>;
  signOut: () => Promise<void>;
  refreshAuth: () => Promise<void>;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  const refreshAuth = async () => {
    setIsLoading(true);
    try {
      const res = await fetch("/api/auth/me", {
        credentials: "include"
      });
      setIsAuthenticated(res.status === 200);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    refreshAuth();
  }, []);

  const signInWithGoogle = async () => {
    await supabase.auth.signInWithOAuth({
      provider: "google",
      options: {
        redirectTo:
          process.env.NEXT_PUBLIC_FRONTEND_URL + "/auth/callback"
      }
    });
  };

  const signOut = async () => {
    await fetch("/api/auth/logout", {
      method: "POST",
      credentials: "include"
    });
    await refreshAuth();
  };

  return (
    <AuthContext.Provider
      value={{
        isAuthenticated,
        isLoading,
        signInWithGoogle,
        signOut,
        refreshAuth
      }}
    >
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
