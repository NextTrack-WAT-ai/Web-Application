import React, { useState } from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Avatar,
  Box,
} from "@mui/material";
import { DragHandle as DragHandleIcon } from "@mui/icons-material";
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
}

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
  isDraggable: boolean;
}

const SortableRow = ({ track, id, isDraggable }: SortableRowProps) => {
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
    <TableRow ref={setNodeRef} style={style}>
      <TableCell sx={{ color: "white", width: "60px" }}>
        {isDraggable && (
          <Box
            {...attributes}
            {...listeners}
            sx={{
              cursor: "grab",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
            }}
          >
            <DragHandleIcon sx={{ color: "#888" }} />
          </Box>
        )}
      </TableCell>
      <TableCell sx={{ color: "white" }}>{track.name}</TableCell>
      <TableCell sx={{ color: "white" }}>{track.artists.join(", ")}</TableCell>
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
  );
};

// Non-sortable row for when dragging is disabled
const StaticRow = ({ track, index }: { track: NextTrack; index: number }) => {
  return (
    <TableRow key={track.trackId || index}>
      <TableCell sx={{ color: "white", width: "60px" }}></TableCell>
      <TableCell sx={{ color: "white" }}>{track.name}</TableCell>
      <TableCell sx={{ color: "white" }}>{track.artists.join(", ")}</TableCell>
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
  );
};

const SongTable: React.FC<SongTableProps> = ({
  tracks,
  onReorder,
  isDraggable = true, // Default to true to maintain backward compatibility
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
    <TableContainer
      component={Paper}
      sx={{ backgroundColor: "#1e1e1e", overflow: "hidden" }}
    >
      {isDraggable ? (
        <DndContext
          sensors={sensors}
          collisionDetection={closestCenter}
          onDragEnd={handleDragEnd}
        >
          <Table>
            <TableHead>
              <TableRow>
                <TableCell sx={{ color: "white", width: "60px" }}></TableCell>
                <TableCell sx={{ color: "white" }}>Song</TableCell>
                <TableCell sx={{ color: "white" }}>Artist</TableCell>
                <TableCell sx={{ color: "white" }}>Album</TableCell>
                <TableCell sx={{ color: "white" }}>Duration</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
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
                    isDraggable={isDraggable}
                  />
                ))}
              </SortableContext>
            </TableBody>
          </Table>
        </DndContext>
      ) : (
        <Table>
          <TableHead>
            <TableRow>
              <TableCell sx={{ color: "white", width: "60px" }}></TableCell>
              <TableCell sx={{ color: "white" }}>Song</TableCell>
              <TableCell sx={{ color: "white" }}>Artist</TableCell>
              <TableCell sx={{ color: "white" }}>Album</TableCell>
              <TableCell sx={{ color: "white" }}>Duration</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {items.map((track, index) => (
              <StaticRow
                key={track.trackId || index}
                track={track}
                index={index}
              />
            ))}
          </TableBody>
        </Table>
      )}
    </TableContainer>
  );
};

export default SongTable;
