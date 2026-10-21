
/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        arena: {
          bg: "#0B0F19",
          panel: "#111827",
          border: "#273244",
          muted: "#94A3B8",
          accent: "#22C55E",
        },
      },
    },
  },
  plugins: [],
};
