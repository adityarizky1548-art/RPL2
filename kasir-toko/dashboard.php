<?php
$judul   = 'Dashboard';
$halaman = 'dashboard';
include 'includes/header.php';
?>

<h4 class="mb-3">Dashboard</h4>

<!-- Data di bawah ini masih contoh (dummy). Nanti diganti data dari database. -->
<div class="row g-3 mb-4">
    <div class="col-6 col-lg-3">
        <div class="card shadow-sm h-100 kartu-ringkasan"><div class="card-body">
            <div class="text-muted">Penjualan Hari Ini</div>
            <div class="angka">Rp 1.250.000</div>
        </div></div>
    </div>
    <div class="col-6 col-lg-3">
        <div class="card shadow-sm h-100 kartu-ringkasan"><div class="card-body">
            <div class="text-muted">Jumlah Transaksi</div>
            <div class="angka">18</div>
        </div></div>
    </div>
    <div class="col-6 col-lg-3">
        <div class="card shadow-sm h-100 kartu-ringkasan"><div class="card-body">
            <div class="text-muted">Barang Terjual</div>
            <div class="angka">64</div>
        </div></div>
    </div>
    <div class="col-6 col-lg-3">
        <div class="card shadow-sm h-100 kartu-ringkasan border-warning"><div class="card-body">
            <div class="text-muted">Stok Menipis</div>
            <div class="angka">3</div>
        </div></div>
    </div>
</div>

<div class="row g-3">
    <div class="col-lg-7">
        <div class="card shadow-sm">
            <div class="card-header fw-bold">Transaksi Terakhir</div>
            <div class="table-responsive">
                <table class="table table-sm mb-0">
                    <thead>
                        <tr><th>No</th><th>Waktu</th><th>Kasir</th><th class="text-end">Total</th></tr>
                    </thead>
                    <tbody>
                        <tr><td>TRX-005</td><td>10:45</td><td>Admin</td><td class="text-end">Rp 85.000</td></tr>
                        <tr><td>TRX-004</td><td>10:12</td><td>Admin</td><td class="text-end">Rp 120.000</td></tr>
                        <tr><td>TRX-003</td><td>09:40</td><td>Admin</td><td class="text-end">Rp 45.500</td></tr>
                        <tr><td>TRX-002</td><td>09:15</td><td>Admin</td><td class="text-end">Rp 230.000</td></tr>
                        <tr><td>TRX-001</td><td>08:50</td><td>Admin</td><td class="text-end">Rp 67.000</td></tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <div class="col-lg-5">
        <div class="card shadow-sm">
            <div class="card-header fw-bold">Barang Stok Menipis</div>
            <div class="table-responsive">
                <table class="table table-sm mb-0">
                    <thead>
                        <tr><th>Nama Barang</th><th class="text-end">Stok</th></tr>
                    </thead>
                    <tbody>
                        <tr class="table-warning"><td>Gula Pasir 1 kg</td><td class="text-end">3</td></tr>
                        <tr class="table-warning"><td>Teh Celup</td><td class="text-end">4</td></tr>
                        <tr class="table-warning"><td>Mi Instan</td><td class="text-end">5</td></tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<?php include 'includes/footer.php'; ?>
