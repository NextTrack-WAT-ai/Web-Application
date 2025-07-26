export interface UserProfile {
  birthdate: string | null;
  country: string | null;
  displayName: string;
  email: string | null;
  externalUrls: {
    spotify: string;
  };
  followers: {
    href: string | null;
    total: number;
  };
  href: string;
  id: string;
  product: string | null;
  type: string;
  uri: string;
  images: {
    url: string;
  }[];
}
