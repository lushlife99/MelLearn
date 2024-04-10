import React from "react";
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

const StyledTableRow = styled(TableRow)(({ theme }) => ({
  // '&:nth-of-type(odd)': {
  //     // backgroundColor: theme.palette.action.hover,
  // },
  // // hide last border
  // '&:last-child td, &:last-child th': {
  //     border: 0,
  // },
}));
export const Rank_yh = (): JSX.Element => {
  return (
    <div className="bg-[#121111] flex flex-row justify-center w-full">
      <div className="bg-[#121111] w-[360px] h-[800px] relative">
        <div className="absolute w-[84px] top-[29px] left-[137px]  font-bold text-white text-[44px] tracking-[0] leading-[normal] whitespace-nowrap">
          랭킹
        </div>
        <div>
          <img
            className="absolute w-[300px] h-[300px] top-[100px] left-[29px] object-cover rounded"
            src="./mardyBUm.jpg"
            alt="?"
          />
        </div>
        <div className="absolute w-[262px] top-[417px] left-[30px] [font-family:'Acme-Regular',Helvetica] font-normal text-white text-[24px] tracking-[1.44px] leading-[normal] whitespace-nowrap">
          Mardy Bum
        </div>
        <div className="absolute w-[306px] h-[194px] top-[475px] left-[29px]">
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
        <div className="h-[51px] top-[716px] left-[29px] absolute w-[306px]">
          <TableContainer component={Paper}>
            <Table sx={{ minWidth: 300 }} aria-label="simple table">
              <TableHead>
                <TableRow>
                  <StyledTableCell>랭크 </StyledTableCell>
                  <StyledTableCell align="center">유저</StyledTableCell>
                  <StyledTableCell align="center">정확도</StyledTableCell>
                </TableRow>
              </TableHead>
            </Table>
          </TableContainer>
        </div>
        <div className="absolute w-[64px] top-[687px] left-[36px] font-normal text-[#8a9a9d] text-[14px] tracking-[0.77px] leading-[normal]">
          내 순위
        </div>
      </div>
    </div>
  );
};
