const {
  addTransaksi,
  getAllTransaksi,
  getTransaksiByIdUser,
  deleteTransaksi,
} = require('./handler');

const routes = [
  {
    method: 'POST',
    path: '/transaksi',
    handler: addTransaksi,
  },
  {
    method: 'GET',
    path: '/transaksi',
    handler: getAllTransaksi,
  },
  {
    method: 'GET',
    path: '/transaksi/{idUser}',
    handler: getTransaksiByIdUser,
  },
  {
    method: 'DELETE',
    path: '/transaksi/{idTransaksi}',
    handler: deleteTransaksi,
  },

];

module.exports = routes;
