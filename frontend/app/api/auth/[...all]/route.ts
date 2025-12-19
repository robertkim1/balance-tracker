import { auth } from "@/lib/auth/auth";
import { toNextJsHandler } from "better-auth/next-js";
// console.log(auth);
export const { GET, POST } = toNextJsHandler(auth);