import React, { useState } from "react";
import { Box, Typography } from "@mui/material";
import {
  DndContext,
  closestCenter,
  KeyboardSensor,
  PointerSensor,
  useSensor,
  useSensors,
  DragEndEvent,
} from "@dnd-kit/core";
import {
  arrayMove,
  SortableContext,
  sortableKeyboardCoordinates,
  useSortable,
  verticalListSortingStrategy,
} from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";
import { NextTrack } from "../models/next-track";

interface SongTableProps {
  tracks: NextTrack[];
  onReorder?: (reorderedTracks: NextTrack[]) => void;
  isDraggable?: boolean;
  isRemix: boolean;
}

// Helper function to format track duration
const formatDuration = (ms: number): string => {
  const minutes = Math.floor(ms / 60000);
  const seconds = Math.floor((ms % 60000) / 1000)
    .toString()
    .padStart(2, "0");
  return `${minutes}:${seconds}`;
};

interface SortableRowProps {
  track: NextTrack;
  id: string;
  index: number;
  isDraggable: boolean;
}

const SortableRow = ({ track, id, index, isDraggable }: SortableRowProps) => {
  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
    isDragging,
  } = useSortable({ id, disabled: !isDraggable });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.5 : 1,
    backgroundColor: isDragging ? "#333" : "transparent",
    zIndex: isDragging ? 1 : 0,
  };

  return (
    <Box
      ref={setNodeRef}
      style={style}
      sx={{
        display: "flex",
        alignItems: "center",
        py: 1,
        px: 2,
        borderBottom: "1px solid #333",
        "&:hover": {
          backgroundColor: "#2a2a2a",
        },
      }}
    >
      <Box sx={{ width: "30px", color: "#aaa", mr: 1 }}>
        <Typography variant="body2">{index + 1}</Typography>
      </Box>

      <Box
        sx={{
          display: "flex",
          alignItems: "center",
          flex: 1,
          mr: 2,
          ...(isDraggable && { cursor: "grab" }),
        }}
        {...(isDraggable && { ...attributes, ...listeners })}
      >
        <Box
          sx={{
            width: 50,
            height: 50,
            mr: 2,
            flexShrink: 0,
            bgcolor: "#333",
            backgroundImage: `url(${
              track.albumCoverUrl || "/api/placeholder/50/50"
            })`,
            backgroundSize: "cover",
          }}
        />
        <Box>
          <Typography variant="body1" sx={{ fontWeight: 500, color: "white" }}>
            {track.name}
          </Typography>
          <Typography variant="body2" sx={{ color: "#aaa" }}>
            {track.artists.join(", ")}
          </Typography>
        </Box>
      </Box>

      <Box sx={{ width: 60, textAlign: "center" }}>
        <Typography variant="body2" sx={{ color: "#aaa" }}>
          {track.tempo}
        </Typography>
      </Box>

      <Box sx={{ width: 60, textAlign: "center" }}>
        <Typography variant="body2" sx={{ color: "#aaa" }}>
          {track.key}
        </Typography>
      </Box>

      <Box sx={{ width: 90, textAlign: "center" }}>
        <Typography variant="body2" sx={{ color: "#aaa" }}>
          {track.loudness}
        </Typography>
      </Box>

      <Box sx={{ width: 60, textAlign: "center" }}>
        <Typography variant="body2" sx={{ color: "#aaa" }}>
          {track.energy}
        </Typography>
      </Box>

      <Box sx={{ width: 70, textAlign: "center" }}>
        <Typography variant="body2" sx={{ color: "#aaa" }}>
          {formatDuration(track.durationMs)}
        </Typography>
      </Box>
    </Box>
  );
};

// Non-sortable row for when dragging is disabled
const StaticRow = ({ track, index }: { track: NextTrack; index: number }) => {
  return (
    <Box
      sx={{
        display: "flex",
        alignItems: "center",
        py: 1,
        px: 2,
        borderBottom: "1px solid #333",
        "&:hover": {
          backgroundColor: "#2a2a2a",
        },
      }}
    >
      <Box sx={{ width: "30px", color: "#aaa", mr: 1 }}>
        <Typography variant="body2">{index + 1}</Typography>
      </Box>

      <Box
        sx={{
          display: "flex",
          alignItems: "center",
          flex: 1,
          mr: 2,
        }}
      >
        <Box
          sx={{
            width: 50,
            height: 50,
            mr: 2,
            flexShrink: 0,
            bgcolor: "#333",
            backgroundImage: `url(${
              track.albumCoverUrl || "/api/placeholder/50/50"
            })`,
            backgroundSize: "cover",
          }}
        />
        <Box>
          <Typography variant="body1" sx={{ fontWeight: 500, color: "white" }}>
            {track.name}
          </Typography>
          <Typography variant="body2" sx={{ color: "#aaa" }}>
            {track.artists.join(", ")}
          </Typography>
        </Box>
      </Box>

      <Box sx={{ width: 60, textAlign: "center" }}>
        <Typography variant="body2" sx={{ color: "#aaa" }}>
          {track.tempo}
        </Typography>
      </Box>

      <Box sx={{ width: 60, textAlign: "center" }}>
        <Typography variant="body2" sx={{ color: "#aaa" }}>
          {track.key}
        </Typography>
      </Box>

      <Box sx={{ width: 90, textAlign: "center" }}>
        <Typography variant="body2" sx={{ color: "#aaa" }}>
          {track.loudness}
        </Typography>
      </Box>

      <Box sx={{ width: 60, textAlign: "center" }}>
        <Typography variant="body2" sx={{ color: "#aaa" }}>
          {track.energy}
        </Typography>
      </Box>

      <Box sx={{ width: 70, textAlign: "center" }}>
        <Typography variant="body2" sx={{ color: "#aaa" }}>
          {formatDuration(track.durationMs)}
        </Typography>
      </Box>
    </Box>
  );
};

const SongTable: React.FC<SongTableProps> = ({
  tracks,
  onReorder,
  isDraggable = true,
  isRemix,
}) => {
  const [items, setItems] = useState(tracks);

  const sensors = useSensors(
    useSensor(PointerSensor),
    useSensor(KeyboardSensor, {
      coordinateGetter: sortableKeyboardCoordinates,
    })
  );

  const handleDragEnd = (event: DragEndEvent) => {
    const { active, over } = event;

    if (over && active.id !== over.id) {
      setItems((items) => {
        const oldIndex = items.findIndex(
          (item) =>
            item.trackId === active.id ||
            items.indexOf(item).toString() === active.id
        );
        const newIndex = items.findIndex(
          (item) =>
            item.trackId === over.id ||
            items.indexOf(item).toString() === over.id
        );

        const reordered = arrayMove(items, oldIndex, newIndex);
        if (onReorder) {
          onReorder(reordered);
        }
        return reordered;
      });
    }
  };

  return (
    <Box
      sx={{
        background: isRemix
          ? "linear-gradient(to bottom, #000000, #1a1a1a, #2a2a15, #3a3a1a, #4a4a1f)" // dim yellow
          : "linear-gradient(to bottom, #000000, #1a1a1a, #153315, #1f4a1f, #2a662a)", // dim green
        borderRadius: 2,
        overflow: "hidden",
        boxShadow: "0px 4px 12px rgba(0, 0, 0, 0.3)",
        border: "1px solid #2a2a2a",
        width: "100%",
        height: "100%",
      }}
    >
      <Box
        sx={{
          display: "flex",
          alignItems: "center",
          borderBottom: "1px solid #333",
          py: 1,
          px: 2,
        }}
      >
        <Box sx={{ width: "30px", mr: 1 }}></Box>
        <Typography variant="body2" sx={{ flex: 1, mr: 2, color: "#aaa" }}>
          Song
        </Typography>
        <Typography
          variant="body2"
          sx={{ width: 60, textAlign: "center", color: "#aaa" }}
        >
          Tempo
        </Typography>
        <Typography
          variant="body2"
          sx={{ width: 60, textAlign: "center", color: "#aaa" }}
        >
          Key
        </Typography>
        <Typography
          variant="body2"
          sx={{ width: 90, textAlign: "center", color: "#aaa" }}
        >
          Loudness
        </Typography>
        <Typography
          variant="body2"
          sx={{ width: 60, textAlign: "center", color: "#aaa" }}
        >
          Energy
        </Typography>
        <Typography
          variant="body2"
          sx={{ width: 70, textAlign: "center", color: "#aaa" }}
        >
          Length
        </Typography>
      </Box>

      {/* Table Body */}
      {isDraggable ? (
        <DndContext
          sensors={sensors}
          collisionDetection={closestCenter}
          onDragEnd={handleDragEnd}
        >
          <SortableContext
            items={items.map(
              (track, index) => track.trackId || index.toString()
            )}
            strategy={verticalListSortingStrategy}
          >
            {items.map((track, index) => (
              <SortableRow
                key={track.trackId || index}
                track={track}
                id={track.trackId || index.toString()}
                index={index}
                isDraggable={isDraggable}
              />
            ))}
          </SortableContext>
        </DndContext>
      ) : (
        <>
          {items.map((track, index) => (
            <StaticRow
              key={track.trackId || index}
              track={track}
              index={index}
            />
          ))}
        </>
      )}
    </Box>
  );
};

export default SongTable;
