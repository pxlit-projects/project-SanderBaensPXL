/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {},
  },
  daisyui: {
    themes: [
      {
        mytheme: {

          "primary": "#0000ff",

          "secondary": "#0054ff",

          "accent": "#de8900",

          "neutral": "#1b091f",

          "base-100": "#edf1ed",

          "info": "#005bf7",

          "success": "#00df81",

          "warning": "#da4d00",

          "error": "#c51b30",
        },
      },
    ],
  },
  plugins: [
    require('daisyui'),
  ],
}

