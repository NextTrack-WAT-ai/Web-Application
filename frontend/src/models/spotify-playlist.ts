export interface SpotifyPlaylist {
  id: string;
  name: string;
  externalUrls: {
    externalUrls: {
      spotify: string;
    };
  };
  images: Image[];
  owner: { display_name: string };
}

type Image = {
  height: number | null;
  width: number | null;
  url: string;
};
