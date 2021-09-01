from flask import Flask
import os
from pythonfiles import augment_data
from pythonfiles import extract_embeddings

app = Flask(__name__)
@app.route('/')

config = {
    apiKey: "AIzaSyBqFROlkrLs0fMirkUoV4Sutn8AkTlBPlQ",
    authDomain: "human-pokedex.firebaseapp.com",
    projectId: "human-pokedex",
    storageBucket: "human-pokedex.appspot.com",
    messagingSenderId: "466324270281",
    appId: "1:466324270281:web:d61e64e5c20932db15b118",
    measurementId: "G-V0DRGWJ51J"
}

def index():
    # Complete this
    augment_data.mainAugment()
    

if __name__ == '__main__':
    app.run(debug = True, host = '0.0.0.0', port = int(os.environ.get('PORT', 8080)))