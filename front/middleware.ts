import { NextRequest, NextResponse } from "next/server";
import { authRoutes, DEFAULT_LOGIN_REDIRECT, publicRoutes } from "./routes";
import { getUserSession } from "./lib/users/getUserSession";

export async function middleware(request: NextRequest) {
    const { nextUrl } = request;
    const { valid, response } = await getUserSession();
    
    const isPublicRoute = publicRoutes.includes(nextUrl.pathname);
    const isAuthRoute = authRoutes.includes(nextUrl.pathname);

    if (isPublicRoute) {
        return;
    }

    if (isAuthRoute) {
        if (valid) {
            return NextResponse.redirect(new URL(DEFAULT_LOGIN_REDIRECT, nextUrl));
        }
        return;
    }

    if (!valid) {
        return NextResponse.redirect(new URL("/login", nextUrl));
    }

    return response || NextResponse.next();
}

export const config = {
    matcher: ["/((?!.+\\.[\\w]+$|_next).*)", "/", "/(api|trpc)(.*)"],
};