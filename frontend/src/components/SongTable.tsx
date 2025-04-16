import React from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Avatar,
} from "@mui/material";
import { NextTrack } from "../models/next-track";

interface SongTableProps {
  tracks: NextTrack[];
}

const formatDuration = (ms: number): string => {
  const minutes = Math.floor(ms / 60000);
  const seconds = Math.floor((ms % 60000) / 1000)
    .toString()
    .padStart(2, "0");
  return `${minutes}:${seconds}`;
};

const SongTable: React.FC<SongTableProps> = ({ tracks }) => {
  return (
    <TableContainer component={Paper} sx={{ backgroundColor: "#1e1e1e" }}>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell sx={{ color: "white" }}>Song</TableCell>
            <TableCell sx={{ color: "white" }}>Artist</TableCell>
            <TableCell sx={{ color: "white" }}>Album</TableCell>
            <TableCell sx={{ color: "white" }}>Duration</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {tracks.map((track, i) => (
            <TableRow key={track.trackId || i}>
              <TableCell sx={{ color: "white" }}>{track.name}</TableCell>
              <TableCell sx={{ color: "white" }}>
                {track.artists.join(", ")}
              </TableCell>
              <TableCell>
                <Avatar
                  src={track.albumCoverUrl || "/api/placeholder/40/40"}
                  alt="Album Cover"
                  variant="square"
                  sx={{ width: 40, height: 40 }}
                />
              </TableCell>
              <TableCell sx={{ color: "white" }}>
                {formatDuration(track.durationMs)}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default SongTable;
