<?php
// Variabel yang diisi dari tiap halaman sebelum include:
//   $judul   = judul halaman (tampil di tab browser)
//   $halaman = nama menu yang sedang aktif
?>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><?= $judul ?? 'Kasir Toko' ?> - Kasir Toko</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="assets/css/style.css" rel="stylesheet">
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-dark bg-success mb-4">
    <div class="container">
        <a class="navbar-brand fw-bold" href="dashboard.php">Kasir Toko</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#menu">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="menu">
            <ul class="navbar-nav me-auto">
                <li class="nav-item">
                    <a class="nav-link <?= ($halaman ?? '') === 'dashboard' ? 'active' : '' ?>" href="dashboard.php">Dashboard</a>
                </li>
                <!-- Tautan di bawah masih "#". Akan diganti di Tahap 2 setelah halamannya dibuat. -->
                <li class="nav-item"><a class="nav-link" href="#">Barang</a></li>
                <li class="nav-item"><a class="nav-link" href="#">Transaksi</a></li>
                <li class="nav-item"><a class="nav-link" href="#">Laporan</a></li>
            </ul>
            <span class="navbar-text me-3">Admin</span>
            <a class="btn btn-outline-light btn-sm" href="login.php">Keluar</a>
        </div>
    </div>
</nav>

<main class="container pb-5">
