"use client";

import { createContext, useContext, ReactNode } from "react";
import { authClient } from "./auth-client";

type AuthContextType = {
  isAuthenticated: boolean;
  isLoading: boolean;
  signInWithGoogle: () => Promise<void>;
  signOut: () => Promise<void>;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const { data: session, isPending } = authClient.useSession();

  const signInWithGoogle = async () => {
    await authClient.signIn.social({
      provider: "google",
      callbackURL: "/" // frontend page after successful login
    });
  };

  const signOut = async () => {
    await authClient.signOut();
  };

  return (
    <AuthContext.Provider
      value={{
        isAuthenticated: !!session,
        isLoading: isPending,
        signInWithGoogle,
        signOut,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("Missing AuthProvider");
  return ctx;
}
