# 开发流程

## 开发前

1. **每个**新需求开发前，先切换到`main`分支

   > `git checkout main`

2. 拉取**最新的**`main`分支

   > `git pull`

3. 基于**最新的**`main`分支，新建分支

   > `git checkout -b your-branch-name`

4. 如果是与他人协同开发同一个需求，并且他人已新建了分支，可使用`git checkout origin/their-branch-name`切换到其分支上进行协作，并勤用`git pull`拉取他人在该分支上的更新

> 如果该需求没有被加入仓库的`issue`中，可以自行新建一个issue，规则如下：
>
> 1. `issue`的标题规则参照[commitlint文档]('./commitlint.md')，括号中标明与该issue相关的项目名（homepage/dashboard）
>    - 如：`feat(homepage&m-homepage): 实现考试管理功能的用户界面`
> 2. `Assignees`选择自己
> 3. `Labels`根据需求的来源（业务/技术）和难度进行选择
> 4. `Type`根据需求的类型进行选择

---

## 开发中

1. 标记你的开发状态

2. 每开发完一个小模块，提交一次代码

   - 每个`commit`都要参照[commitlint文档]('./commitlint.md')的格式要求书写
   - `commit message`应能够对应本次提交的内容，除了一些小需求外，不要照抄issue标题
     - 如：提交后lint报错，修改后重新提交，可写为`fix: fix lint error`
   - 初次提交时，需使用`git push --set-upstream origin your-branch-name`关联远程分支，后续直接`git push`即可

3. 在GitHub上创建`pr`

   - 标题可以与issue相同，也可根据具体实现另写，但其格式仍需遵循[commitlint]('./commitlint.md')的要求，并且要在括号中写明与该pr相关的项目名
   - 在`description`中，使用`close #xxx`的形式关联`issue`，其中`xxx`是`issue`的编号

4. 创建`pr`后，每次提交都会触发`lint and format`的`github action`，应关注该ci的结果，如有报错，根据报错内容处理后再次提交

5. 开发移动端项目前，可安装`微信开发者工具`，便于调试微信登录等功能。项目在本地启动后会给出多个本地ip，部分ip在手机微信中无法打开，可以逐个尝试。

---

## 开发后

1. 在对应的`pr`页面右侧的`Reviewers`中`Request`一人对你的代码进行review

2. 关注评论，根据review的意见修改代码

3. 代码修改完成后，通知`reviewer`再次review（如果reviewer使用了request change，可直接点击pr页的`re-request`）

4. 对应的评论修改通过后，可点击`Resolve conversation`关闭该评论

5. **不要**自行`merge`代码！（`Merge pull request`和`Squash and merge`两个按钮都不要点，等review通过后统一合）

6. 代码被合并到`main`分支，并且`pre-release-apps`的ci完成后，可在对应的测试服进行测试
