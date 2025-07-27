export interface NextTrack {
  trackId: string;
  name: string;
  artists: string[]; // array of artist names
  albumCoverUrl: string;
  durationMs: number;
  trackUri: string;
  trackIndex?: number;
  tempo?: number;
  key?: number;
  loudness?: number;
  energy?: number;
  danceability?: number;
  mode?: string;
  speechiness?: number;
  acousticness?: number;
  instrumentalness?: number;
  liveness?: number;
  valence?: number;
}
