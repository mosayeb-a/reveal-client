package app.ma.reveal.common

import androidx.compose.ui.unit.dp

const val REVEAL_DIR = "reveal"
const val SLIDES_DIR = "slides"
const val INDEX_FILE = "presentation.html"
const val FILE_POLLING_INTERVAL_MS = 2000L
val BOTTOM_BAR_HEIGHT = 56.dp

const val HTML_TEMPLATE = """
    <!doctype html>
    <html lang="en">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>%s</title>
        <link rel="stylesheet" href="%s/dist/reset.css">
        <link rel="stylesheet" href="%s/dist/reveal.css">
        <link rel="stylesheet" href="%s/dist/theme/white.css">
        <link rel="stylesheet" href="%s/plugin/highlight/monokai.css">
    </head>
    <body>
        <div class="reveal">
            <div class="slides">
                %s
            </div>
        </div>
        <script src="%s/dist/reveal.js"></script>
        <script src="%s/plugin/markdown/markdown.js"></script>
        <script src="%s/plugin/highlight/highlight.js"></script>
        <script src="%s/plugin/notes/notes.js"></script>
        <script src="%s/plugin/math/math.js"></script>
        <script>
            try {
                let deck = new Reveal({
                    hash: true,
                    plugins: [ RevealMarkdown, RevealHighlight, RevealNotes, RevealMath ]
                });
                deck.initialize().then(() => {
                    window.revealDeck = deck;
                    console.log('Reveal.js initialized successfully');
                }).catch(e => {
                    console.error('Reveal.js initialization failed:', e);
                });
            } catch (e) {
                console.error('Error:', e);
            }
        </script>
    </body>
    </html>
"""

