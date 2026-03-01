#!/bin/bash

# Burnout Prevention Project - Run & Test Script
# Скрипт для запуска и тестирования проекта

set -e

# Цвета для вывода
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Директории
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$SCRIPT_DIR/pacificapp__backend"
ANDROID_DIR="$SCRIPT_DIR/pacificapp_android"

# Функции для вывода
info() { echo -e "${BLUE}[INFO]${NC} $1"; }
success() { echo -e "${GREEN}[OK]${NC} $1"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; }

# Проверка зависимостей
check_dependencies() {
    info "Проверка зависимостей..."

    local missing=0

    if ! command -v go &> /dev/null; then
        error "Go не установлен. Установите: brew install go"
        missing=1
    else
        success "Go $(go version | awk '{print $3}')"
    fi

    if ! command -v docker &> /dev/null; then
        error "Docker не установлен. Установите Docker Desktop"
        missing=1
    else
        success "Docker $(docker --version | awk '{print $3}' | tr -d ',')"
    fi

    if ! command -v protoc &> /dev/null; then
        warn "protoc не установлен. Установите: brew install protobuf"
    else
        success "protoc $(protoc --version | awk '{print $2}')"
    fi

    if [ $missing -eq 1 ]; then
        error "Отсутствуют необходимые зависимости"
        exit 1
    fi

    echo ""
}

# Запуск PostgreSQL
start_postgres() {
    info "Запуск PostgreSQL..."

    # Проверяем, запущен ли уже контейнер
    if docker ps --format '{{.Names}}' | grep -q "burnout_postgres"; then
        success "PostgreSQL уже запущен"
        return 0
    fi

    # Проверяем, есть ли остановленный контейнер
    if docker ps -a --format '{{.Names}}' | grep -q "burnout_postgres"; then
        info "Запуск остановленного контейнера..."
        docker start burnout_postgres
    else
        info "Создание нового контейнера PostgreSQL..."
        docker run -d \
            --name burnout_postgres \
            -e POSTGRES_USER=postgres \
            -e POSTGRES_PASSWORD=postgres \
            -e POSTGRES_DB=burnout_prevention \
            -p 5432:5432 \
            postgres:15-alpine
    fi

    # Ждем готовности PostgreSQL
    info "Ожидание готовности PostgreSQL..."
    for i in {1..30}; do
        if docker exec burnout_postgres pg_isready -U postgres &> /dev/null; then
            success "PostgreSQL готов к работе"
            return 0
        fi
        sleep 1
    done

    error "PostgreSQL не запустился за 30 секунд"
    exit 1
}

# Остановка PostgreSQL
stop_postgres() {
    info "Остановка PostgreSQL..."
    if docker ps --format '{{.Names}}' | grep -q "burnout_postgres"; then
        docker stop burnout_postgres
        success "PostgreSQL остановлен"
    else
        warn "PostgreSQL не запущен"
    fi
}

# Запуск тестов backend
run_backend_tests() {
    info "Запуск тестов backend..."
    cd "$BACKEND_DIR"

    # Проверяем модули Go
    if [ ! -f "go.sum" ]; then
        info "Загрузка зависимостей Go..."
        go mod download
    fi

    # Запуск тестов
    echo ""
    info "Выполнение тестов..."
    if go test ./... -v -count=1 2>&1 | tee /tmp/backend_tests.log; then
        echo ""
        success "Все тесты backend прошли успешно!"
    else
        echo ""
        error "Некоторые тесты не прошли. Смотрите лог: /tmp/backend_tests.log"
        return 1
    fi

    cd "$SCRIPT_DIR"
}

# Запуск backend сервера
start_backend() {
    info "Запуск backend сервера..."
    cd "$BACKEND_DIR"

    # Проверяем proto файлы
    if [ ! -d "pkg/proto/user" ] || [ -z "$(ls -A pkg/proto/user 2>/dev/null)" ]; then
        info "Генерация proto файлов..."
        make proto 2>/dev/null || warn "Proto генерация пропущена"
    fi

    # Создаем .env если не существует
    if [ ! -f ".env" ]; then
        info "Создание .env файла..."
        cat > .env << EOF
DB_HOST=localhost
DB_PORT=5432
DB_USER=postgres
DB_PASSWORD=postgres
DB_NAME=burnout_prevention
DB_SSLMODE=disable
JWT_SECRET=development-secret-key-12345
JWT_EXPIRATION_HOURS=24
GRPC_PORT=50051
DEBUG=true
EOF
        success ".env файл создан"
    fi

    info "Запуск gRPC сервера на порту 50051..."
    go run cmd/server/main.go &
    BACKEND_PID=$!
    echo $BACKEND_PID > /tmp/backend.pid

    sleep 2

    if kill -0 $BACKEND_PID 2>/dev/null; then
        success "Backend сервер запущен (PID: $BACKEND_PID)"
        info "gRPC сервер: localhost:50051"
    else
        error "Не удалось запустить backend сервер"
        return 1
    fi

    cd "$SCRIPT_DIR"
}

# Остановка backend
stop_backend() {
    info "Остановка backend сервера..."
    if [ -f /tmp/backend.pid ]; then
        PID=$(cat /tmp/backend.pid)
        if kill -0 $PID 2>/dev/null; then
            kill $PID
            rm /tmp/backend.pid
            success "Backend сервер остановлен"
        else
            warn "Backend сервер не запущен"
            rm /tmp/backend.pid
        fi
    else
        # Попробуем найти процесс по имени
        pkill -f "go run cmd/server/main.go" 2>/dev/null && success "Backend сервер остановлен" || warn "Backend сервер не найден"
    fi
}

# Тестирование Android (только проверка сборки)
check_android() {
    info "Проверка Android проекта..."

    if [ ! -d "$ANDROID_DIR" ]; then
        warn "Android проект не найден: $ANDROID_DIR"
        return 1
    fi

    cd "$ANDROID_DIR"

    if [ -f "gradlew" ]; then
        info "Проверка сборки Android..."
        ./gradlew check --no-daemon 2>&1 | tail -20
    else
        warn "gradlew не найден. Android проект требует настройки в Android Studio."
    fi

    cd "$SCRIPT_DIR"
}

# Показать статус
show_status() {
    echo ""
    echo "=== Статус проекта ==="
    echo ""

    # PostgreSQL
    if docker ps --format '{{.Names}}' | grep -q "burnout_postgres"; then
        success "PostgreSQL: запущен (порт 5432)"
    else
        warn "PostgreSQL: остановлен"
    fi

    # Backend
    if [ -f /tmp/backend.pid ] && kill -0 $(cat /tmp/backend.pid) 2>/dev/null; then
        success "Backend: запущен (gRPC порт 50051)"
    else
        warn "Backend: остановлен"
    fi

    echo ""
}

# Показать справку
show_help() {
    echo ""
    echo "Burnout Prevention Project - Run & Test Script"
    echo ""
    echo "Использование: $0 [команда]"
    echo ""
    echo "Команды:"
    echo "  start       - Запустить PostgreSQL и Backend"
    echo "  stop        - Остановить все сервисы"
    echo "  restart     - Перезапустить все сервисы"
    echo "  test        - Запустить тесты backend"
    echo "  status      - Показать статус сервисов"
    echo "  db          - Только запустить PostgreSQL"
    echo "  db-stop     - Остановить PostgreSQL"
    echo "  backend     - Только запустить backend"
    echo "  backend-stop - Остановить backend"
    echo "  android     - Проверить Android проект"
    echo "  all         - Запустить всё и выполнить тесты"
    echo "  help        - Показать эту справку"
    echo ""
    echo "Примеры:"
    echo "  $0 start    # Запустить проект"
    echo "  $0 test     # Запустить тесты"
    echo "  $0 all      # Полный цикл: запуск + тесты"
    echo ""
}

# Главная функция
main() {
    echo ""
    echo "======================================"
    echo "  Burnout Prevention Project"
    echo "======================================"
    echo ""

    case "${1:-help}" in
        start)
            check_dependencies
            start_postgres
            start_backend
            show_status
            ;;
        stop)
            stop_backend
            stop_postgres
            show_status
            ;;
        restart)
            stop_backend
            stop_postgres
            sleep 2
            start_postgres
            start_backend
            show_status
            ;;
        test)
            check_dependencies
            start_postgres
            run_backend_tests
            ;;
        status)
            show_status
            ;;
        db)
            check_dependencies
            start_postgres
            ;;
        db-stop)
            stop_postgres
            ;;
        backend)
            check_dependencies
            start_backend
            ;;
        backend-stop)
            stop_backend
            ;;
        android)
            check_android
            ;;
        all)
            check_dependencies
            start_postgres
            run_backend_tests
            start_backend
            show_status
            echo ""
            info "Проект готов к использованию!"
            info "gRPC сервер: localhost:50051"
            info "Для остановки: $0 stop"
            ;;
        help|--help|-h)
            show_help
            ;;
        *)
            error "Неизвестная команда: $1"
            show_help
            exit 1
            ;;
    esac
}

# Запуск
main "$@"
