import { ThemeToggle } from "@/components/theme-toggle";

export default function Home() {
  return (
    <main className="flex min-h-screen flex-col items-center justify-center gap-4 p-8">
      <div className="absolute right-4 top-4">
        <ThemeToggle />
      </div>

      <h1 className="text-5xl font-bold tracking-tight text-foreground">
        hello
      </h1>
    </main>
  );
}
