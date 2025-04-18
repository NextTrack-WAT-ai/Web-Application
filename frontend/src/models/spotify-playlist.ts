export interface SpotifyPlaylist {
  id: string;
  name: string;
  images: Image[];
  owner: { display_name: string };
}

type Image = {
  height: number | null;
  width: number | null;
  url: string;
};
