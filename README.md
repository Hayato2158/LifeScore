# LifeScore アプリケーション設計概要

## 1. はじめに

LifeScoreは、日々の活動や気分のスコアを記録・管理するためのAndroidアプリケーションです。
このドキュメントでは、本アプリケーションの設計方針、アーキテクチャ、および主要な技術要素について概説します。

## 2. アーキテクチャ

本プロジェクトでは、Android公式が推奨するアプリ アーキテクチャガイドに準拠し、**MVVM (Model-View-ViewModel) パターン** をベースに、**Repositoryパターン** を組み合わせた設計を採用しています。これにより、関心の分離、テスト容易性の向上、保守性の向上を目指しています。

主要なレイヤー構成は以下の通りです。

*   **UIレイヤー (View)**:
    *   役割: ユーザーインターフェースの表示とユーザー入力の受付。
    *   担当: `Activity` (`MainActivity`)、`Composable` 関数群 (`ScoreHomeScreen` など)。
    *   状態の監視とUI更新: ViewModelから公開される状態 (StateFlowなど) を監視し、UIをリアクティブに更新します。
    *   ユーザーイベントの通知: ユーザー操作をViewModelに通知します。

*   **ViewModelレイヤー**:
    *   役割: UIに関連するデータを保持・管理し、UIロジックを実行。UIレイヤーとデータレイヤー間の橋渡し。
    *   担当: `ViewModel` を継承したクラス (`ScoresViewModel`)。
    *   データの公開: UIが必要とするデータを `StateFlow` や `LiveData` (本プロジェクトでは主に `StateFlow`) を通じて公開します。
    *   ビジネスロジックの委譲: 複雑なデータ操作やビジネスロジックはリポジトリに委譲します。
    *   ライフサイクル対応: Androidのライフサイクルを意識したデータ管理を行います。

*   **データレイヤー (Repository + Data Sources)**:
    *   役割: アプリケーションデータの操作と管理、ビジネスロジックの実行。
    *   **Repository (`ScoreRepository`)**:
        *   データアクセスの一元的な窓口。ViewModelからのデータ要求を受け付け、適切なデータソースからデータを取得・提供します。
        *   データソースの抽象化。ViewModelはデータがローカルDBなのかリモートサーバーなのかを意識しません。
        *   複数のデータソースからのデータを組み合わせるロジックも担当できます (本プロジェクトでは現在ローカルDBのみ)。
    *   **Data Sources**:
        *   **Local Data Source (`ScoreDao`, `AppDatabase`)**: Room永続ライブラリを使用し、SQLiteデータベースへのデータの永続化を行います。DAO (Data Access Object) を通じてCRUD操作を提供します。
        *   **Remote Data Source (該当なし)**: 必要に応じてネットワークAPI経由でデータを取得するコンポーネント。現在は未使用。
    *   **DIコンポーネント (`AppModule`, `Clock`)**:
        *   `Clock` の注入により、日付や時刻に依存するロジックのテスト容易性を確保しています。

## 3. 主要コンポーネントと技術スタック

*   **言語**: Kotlin (コルーチン、Flowを積極的に活用)
*   **UIフレームワーク**: Jetpack Compose (宣言的なUI構築)
*   **状態管理 (ViewModel内)**: `StateFlow`
*   **依存性注入 (DI)**: Hilt (Dagger Hilt)
    *   コンストラクタインジェクションを基本とし、`@HiltViewModel`, `@AndroidEntryPoint`, `@Module`, `@Provides`, `@Singleton` などを活用。
*   **データベース**: Room Persistence Library (SQLiteの抽象化レイヤー)
*   **非同期処理**: Kotlin Coroutines (`suspend`関数, `Flow`, `viewModelScope`, `Dispatchers`)
*   **アーキテクチャコンポーネント**: ViewModel, LiveData (限定的に使用可能性あり), Lifecycle

## 4. データフローの例 (スコア保存時)

1.  **UI (`ScoreHomeScreen`)**: ユーザーがスコアボタンをタップ。
2.  **UI (`ScoreHomeScreen`)**: `onClick` イベントが発火し、ViewModelのメソッド (`vm.saveToday(score)`) を呼び出す。
3.  **ViewModel (`ScoresViewModel`)**: `saveToday(score)` メソッド内で、注入された `ScoreRepository` の `saveToday(score)` メソッドを呼び出す。
4.  **Repository (`ScoreRepository`)**: `saveToday(score)` メソッド内で、注入された `Clock` を使用して現在の日付を取得し、`ScoreRecord` オブジェクトを作成。注入された `ScoreDao` の `upsert(record)` メソッドを呼び出す。
5.  **DAO (`ScoreDao`)**: `@Upsert` アノテーションにより、Roomが対応するSQLを実行し、データをデータベースに保存（または更新）。
6.  **(データ更新の反映)**: もし `ScoreRepository` が `Flow` でデータを提供していれば、データベースの変更が自動的に `Flow` に通知され、それを監視しているViewModelおよびUIが更新される (例: スコアリストの表示など)。

## 5. テスト容易性への配慮

*   **Repositoryパターン**: データアクセスロジックを分離することで、ViewModelやUIとは独立してテスト可能。
*   **DI (Hilt)**: 依存関係を外部から注入することで、テスト時にモックやフェイクオブジェクトへの差し替えが容易。
    *   例: `ScoreDao` や `Clock` をテスト用の実装に差し替える。
*   **ViewModel**: UIロジックがViewModelに集約されるため、UIフレームワークに依存しないユニットテストが可能。

## 6. ディレクトリ構成 (主なもの)
app/src/main/java/io/github/hayato2158/lifescore/ ├── data/             # データ層 (Repository, DAO, Entity, Database, MonthlySummaryなど) ├── di/               # 依存性注入モジュール (AppModuleなど) ├── ui/               # UI層 (Composable関数, Themeなど) └── viewmodel/        # ViewModel層 (ScoresViewModelなど)

## 7. 今後の展望 (任意)

*   リモートサーバーとのデータ同期機能の追加。
*   より詳細な統計表示機能。
*   ユニットテストおよびUIテストの拡充。

---
