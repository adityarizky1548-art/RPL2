<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Login - Kasir Toko</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="assets/css/style.css" rel="stylesheet">
</head>
<body class="login-wrapper d-flex align-items-center justify-content-center">

    <div class="card login-card shadow-lg w-100 mx-3">
        <div class="card-body p-4 p-md-5">
            <h3 class="text-center fw-bold mb-1">Kasir Toko</h3>
            <p class="text-center text-muted mb-4">Silakan masuk untuk melanjutkan</p>

            <!-- Sementara form ini langsung menuju dashboard.php.
                 Nanti di tahap backend, action diganti ke file proses login. -->
            <form action="dashboard.php" method="get">
                <div class="mb-3">
                    <label for="username" class="form-label">Username</label>
                    <input type="text" class="form-control form-control-lg" id="username" name="username" placeholder="Masukkan username">
                </div>
                <div class="mb-4">
                    <label for="password" class="form-label">Password</label>
                    <input type="password" class="form-control form-control-lg" id="password" name="password" placeholder="Masukkan password">
                </div>
                <button type="submit" class="btn btn-success btn-lg w-100">Masuk</button>
            </form>
        </div>
    </div>

</body>
</html>
