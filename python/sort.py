# 入力された数列を並べ替えるプログラム

# 数列を標準入力より受け取る
sequence = input("数列を入力してください(※1,2,3,4というような形式で入力してください):")
# 数列をリスト化
sequence_list = sequence.split(",")
# 数値でないものは除外する
filtered_sequence = []
for element in sequence_list:
    try:
        tmp = int(element)
        filtered_sequence.append(tmp)
    except ValueError:
        print(f"要素{element}は数値化できないので除外します")

# sequence_listのアドレスをfiltered_sequenceに変更
sequence_list = filtered_sequence

# 数列リストの中身が文字列となっているのでint型に変更
sequence_list = [int(num) for num in sequence_list]

print(sequence_list)
# 昇順か、降順かを選択
while True:
    try:
        how_sort = int(input("昇順か、降順かを選んでください(1:昇順,2:降順)->:"))
        if 1 <= how_sort <= 2:
            break
        else:
            print("1,2以外の数値を入力しないでください")
    except ValueError:
        print("エラー、数値出ないものが入力されました")


# 昇順だった場合
if how_sort == 1:
    sequence_list.sort()
    print("ソート結果は以下のとおりです")
    print(sequence_list)
# 降順だった場合
elif how_sort ==2:
    sequence_list.sort(reverse=True)
    print("ソート結果は以下のとおりです")
    print(sequence_list)
