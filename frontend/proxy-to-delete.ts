// import { NextRequest, NextResponse } from "next/server";

// export function proxy(req: NextRequest) {
//   const jwt = req.cookies.get("jwt");

//   if (!jwt) {
//     return NextResponse.redirect(new URL("/", req.url));
//   }

//   return NextResponse.next();
// }