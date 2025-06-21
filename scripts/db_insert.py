import kagglehub
import pandas as pd
from pymongo import MongoClient

import certifi
import os 
from dotenv import load_dotenv

load_dotenv()


# Load data (assuming the main file with audio features is in CSV format)
# Adjust the file name as needed based on the downloaded contents
csv_file_path = f"./tracks.csv"

print("Reading CSV...")
df = pd.read_csv(csv_file_path)

# Connect to MongoDB
client = MongoClient(os.getenv("MONGO_URI"),tlsCAFile=certifi.where())
db = client["next_track"]
collection = db["million_song_audio_features"]

# Define the fields we want to extract
fields = [
    "song_name", "artist_name", "danceability", "energy", "key", "loudness", "mode",
    "speechiness", "acousticness", "instrumentalness", "liveness",
    "valence", "tempo"
]
# Ensure field names match those in your CSV
missing = [f for f in fields if f not in df.columns]
if missing:
    raise ValueError(f"The following required fields are missing from the CSV: {missing}")

print("[PROCESSING DOCUMENTS]")
# Iterate and insert
documents = []
for _, row in df.iterrows():
    print(f"[PROCESSING DOCUMENT] {row["song_name"]}")
    doc = {
        # "trackId": row["track_id"], not necessary with new CSV file 
        "name": row["song_name"],
        "artist": row["artist_name"],
        # "year": int(row["year"]), this is not in the new CSV file either
        "danceability": float(row["danceability"]),
        "energy": float(row["energy"]),
        "key": int(row["key"]),
        "loudness": float(row["loudness"]),
        "mode": int(row["mode"]),
        "speechiness": float(row["speechiness"]),
        "acousticness": float(row["acousticness"]),
        "instrumentalness": float(row["instrumentalness"]),
        "liveness": float(row["liveness"]),
        "valence": float(row["valence"]),
        "tempo": float(row["tempo"]),
        # "timeSignature": int(row["time_signature"]),
        # "tags": row["tags"] also not in the new csv file 
        # acousticness,liveness,duration,danceability,speechiness,valence,loudness,energy,mode,key,instrumentalness,tempo,song_name,artist_name
    }
    documents.append(doc)

print("Inserting documents into MongoDB...")
# Bulk insert
if documents:
    collection.insert_many(documents)
    print(f"Inserted {len(documents)} documents.")
else:
    print("No documents to insert.")