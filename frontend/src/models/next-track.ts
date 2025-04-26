export interface NextTrack {
  trackId: string;
  name: string;
  artists: string[]; // array of artist names
  albumCoverUrl: string;
  durationMs: number;
  trackUri: string;
  trackIndex?: number;
}
