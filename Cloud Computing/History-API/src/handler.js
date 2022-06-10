const { nanoid } = require('nanoid');
const transaksi = require('./transaksi');

const addTransaksi = (request, h) => {
  const {
    idUser,
    jumlah,
    pihak,
    jenis,
  } = request.payload;
  const idTransaksi = nanoid(16);
  const date = new Date();
  const tanggal = `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
  const waktu = `${date.getHours()}:${date.getMinutes()}:${date.getSeconds()}`;

  if (!idUser && !jumlah && !pihak && !jenis) {
    return h.response({
      statusCode: 400,
      error: 'Bad Request',
      message: 'Invalid Input',
    }).code(400);
  }
  const newTransaksi = {
    idTransaksi,
    idUser,
    pihak,
    jumlah,
    jenis,
    tanggal,
    waktu,
  };
  transaksi.push(newTransaksi);

  const isSuccess = transaksi.filter((transaction) => transaction.idTransaksi === idTransaksi).length > 0;
  if (isSuccess) {
    const response = h.response({
      status: 'success',
      message: 'Transaksi berhasil ditambahkan',
      data: {
        TransaksiId: idTransaksi,
      },
    }).code(201);
    return response;
  }

  const response = h.response({
    status: 'error',
    message: 'Transaksi gagal ditambahkan',
  }).code(500);
  return response;
};

const getAllTransaksi = (request, h) => {
  const response = h.response({
    status: 'success',
    data: transaksi,
  }).code(200);
  return response;
};

const getTransaksiByIdUser = (request, h) => {
  const { idUser } = request.params;
  const hist = transaksi.filter((transaction) => transaction.idUser === idUser);
  if (hist) {
    const response = h.response({
      status: 'success',
      data: hist,
    }).code(200);
    return response;
  }
  const response = h.response({
    status: 'error',
    message: 'Transaksi tidak ditemukan',
  }).code(404);
  return response;
};

const deleteTransaksi = (request, h) => {
  const { idTransaksi } = request.params;
  const index = transaksi.findIndex((transaction) => transaction.idTransaksi === idTransaksi);
  if (index > -1) {
    transaksi.splice(index, 1);
    const response = h.response({
      status: 'success',
      message: 'Transaksi berhasil dihapus',
    }).code(200);
    return response;
  }
  const response = h.response({
    status: 'error',
    message: 'Transaksi tidak ditemukan',
  }).code(404);
  return response;
};

module.exports = {
  addTransaksi,
  getAllTransaksi,
  getTransaksiByIdUser,
  deleteTransaksi,
};
