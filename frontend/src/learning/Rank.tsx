import React, { useEffect, useState } from "react";
import {
  Paper,
  TableBody,
  TableCell,
  tableCellClasses,
  TableContainer,
  TableHead,
  TableRow,
} from "@mui/material";
import { styled, Table } from "@mui/joy";
import { useLocation, useNavigate } from "react-router-dom";
import axiosApi from "../api";
import { IoIosArrowRoundBack } from "react-icons/io";

interface IMember {
  id: number;
  langType: string;
  level: string;
  levelPoint: number;
  memberId: string;
  name: string;
}
interface ScoreList {
  [key: string]: number;
}
interface IRank {
  id: number;
  musicId: string;
  score_list: ScoreList;
}

function createData(rank: number, userName: string, accuracy: number) {
  return { rank, userName, accuracy };
}

const rows = [
  createData(1, "user1", 90.9),
  createData(2, "user2", 80.44),
  createData(3, "user3", 55.52),
];

const StyledTableCell = styled(TableCell)(({ theme }) => ({
  [`&.${tableCellClasses.head}`]: {
    backgroundColor: "#007aff",
    color: theme.palette.common.white,
    fontSize: 16,
  },
  [`&.${tableCellClasses.body}`]: {
    backgroundColor: "#007aff",
    fontSize: 16,
    color: theme.palette.common.white,
  },
}));

export const Rank = (): JSX.Element => {
  const location = useLocation();
  const { trackId, track } = location.state;
  const navigate = useNavigate();
  const [rank, setRank] = useState<[string, unknown][]>([]);
  const [myScore, setMyScore] = useState<number | undefined>();
  const [member, setMember] = useState<IMember>();
  const [memberId, setMemberId] = useState<string>();

  const getRank = async () => {
    const res = await axiosApi.get(
      `/api/problem/speaking/ranking?musicId=${track.id.replace(/['"]+/g, "")}`
    );

    const entries = Object.entries(res.data.score_list);
    const currentMemberScore = entries.find(([key]) => key === memberId)?.[1];
    if (typeof currentMemberScore === "number") {
      setMyScore(currentMemberScore);
    }

    const sortedEntries: [string, unknown][] = entries.sort((a, b) => {
      if (a[0] < b[0]) return -1;
      if (a[0] > b[0]) return 1;
      return 0;
    });

    setRank(sortedEntries);
    console.log(sortedEntries);
  };
  const getMember = async () => {
    const res = await axiosApi.get("/api/member/info");

    setMember(res.data);
    setMemberId(res.data.memberId);
  };

  useEffect(() => {
    getRank();
    getMember();
  }, []);
  return (
    <div className="bg-[black] flex flex-row justify-center w-full h-screen font-roboto">
      <div className="bg-[black] overflow-hidden w-[450px] h-screen relative flex flex-col px-8 border border-white">
        <div className="mt-4">
          <IoIosArrowRoundBack
            onClick={() => navigate(-1)}
            className="w-8 h-8 fill-white hover:opacity-60"
          />
        </div>
        <div className="flex items-center justify-center w-full my-4">
          <span className="text-4xl font-bold text-white ">랭킹</span>
        </div>
        <div className="flex items-center justify-center">
          <img
            className="w-100 h-100"
            src={track.album.images[0].url}
            alt="Album Cover"
          />
        </div>
        <div className="flex justify-start mt-4">
          <span className="text-3xl font-bold text-white">{track.name}</span>
        </div>
        <div className="my-4">
          <TableContainer component={Paper}>
            <Table sx={{ minWidth: 300 }} aria-label="simple table">
              <TableHead>
                <TableRow>
                  <StyledTableCell>랭크 </StyledTableCell>
                  <StyledTableCell align="center">유저</StyledTableCell>
                  <StyledTableCell align="center">정확도</StyledTableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {rows.map((row) => (
                  <TableRow
                    key={row.rank}
                    sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
                  >
                    <StyledTableCell component="th" scope="row">
                      {row.rank}
                    </StyledTableCell>
                    <StyledTableCell align="left">
                      {row.userName}
                    </StyledTableCell>
                    <StyledTableCell align="left">
                      {row.accuracy}
                    </StyledTableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </div>

        <div className="">
          <div className="mb-2">
            <span className="text-xl font-bold text-[#8A9A9D]">내 순위</span>
          </div>
          <TableContainer component={Paper}>
            <Table sx={{ minWidth: 300 }} aria-label="simple table">
              <TableHead>
                <TableRow>
                  <StyledTableCell>랭크 </StyledTableCell>
                  <StyledTableCell align="center">
                    {member?.memberId}
                  </StyledTableCell>
                  <StyledTableCell align="center">{myScore}</StyledTableCell>
                </TableRow>
              </TableHead>
            </Table>
          </TableContainer>
        </div>
      </div>
    </div>
  );
};
