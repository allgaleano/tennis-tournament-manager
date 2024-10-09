import type { Metadata } from "next";
import "@/resources/globals.css";

export const metadata: Metadata = {
  title: "Tennis Tournament Manager",
  description: "An app to manage all types of tennis tournaments",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="es">
      <body>
        {children}
      </body>
    </html>
  );
}
