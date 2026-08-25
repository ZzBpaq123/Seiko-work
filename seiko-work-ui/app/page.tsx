import { ThemeToggle } from "@/components/theme-toggle";
import { MobiusBackground } from "@/components/mobius-background";

export default function Home() {
  return (
    <>
      <MobiusBackground />
      <main className="relative z-10 flex min-h-screen flex-col items-center justify-center gap-4 p-8">
        <div className="absolute right-4 top-4">
          <ThemeToggle />
        </div>

        <h1 className="text-5xl font-bold tracking-tight text-[#171717]">
          hello
        </h1>
      </main>
    </>
  );
}
