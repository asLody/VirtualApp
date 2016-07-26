//
// Created by Xfast on 2016/7/21.
//
#include "Hook.h"

static std::map<std::string/*orig_path*/, std::string/*new_path*/> IORedirectMap;
static std::map<std::string/*orig_path*/, std::string/*new_path*/> RootIORedirectMap;

static inline void hook_template(const char *lib_so, const char *symbol, void *new_func, void **old_func) {
    LOGI("hook symbol=%s, new_func=%p, old_func=%p", symbol, new_func, *old_func);
    void *handle = dlopen(lib_so, RTLD_GLOBAL | RTLD_LAZY);
    if (handle == NULL) {
        LOGW("can't hook %s in %s: 'dlopen' %s FAILED!!!", symbol, lib_so, lib_so);
        return;
    }
    void *addr = dlsym(handle, symbol);
    if (addr == NULL) {
        LOGW("can't hook %s in %s: 'dlsym' %s func FAILED!!!", symbol, lib_so, symbol);
        return;
    }
    elfHookDirect((unsigned int) (addr), new_func, old_func);
    LOGI("Hook %s in %s SUCCESS!", symbol, lib_so);
    dlclose(handle);
}




static inline bool startWith(const std::string &str, const std::string &prefix)
{
    return str.find(prefix) == 0;
}


static inline bool endWith(const std::string &str, const char &suffix) {
    return *(str.end() - 1) == suffix;
}

static void add_pair(const char *_orig_path, const char *_new_path) {
    std::string origPath = std::string(_orig_path);
    std::string newPath = std::string(_new_path);
    IORedirectMap.insert(std::pair<std::string, std::string>(origPath, newPath));
    if (endWith(origPath, '/')) {
        RootIORedirectMap.insert(
                std::pair<std::string, std::string>(
                        origPath.substr(0, origPath.length() - 1),
                        newPath.substr(0, newPath.length() - 1))
        );
    }
}


const char *match_redirected_path(const char *_path) {
    if(_path == NULL) {
        return NULL;
    }
    std::string path(_path);
    if (path.length() <= 1) {
        return _path;
    }
    std::map<std::string, std::string>::iterator iterator;
    iterator = RootIORedirectMap.find(path);
    if (iterator != RootIORedirectMap.end()) {
        return iterator->second.c_str();
    }

    for (iterator = IORedirectMap.begin(); iterator != IORedirectMap.end(); iterator++) {
        std::string prefix = iterator->first;
        std::string new_prefix = iterator->second;
        if (startWith(path, prefix)) {
            std::string new_path = new_prefix + path.substr(prefix.length(), path.length());
            return new_path.c_str();
        }
    }
    return _path;
}


void HOOK::redirect(const char *org_path, const char *new_path) {
    LOGI("native add redirect: from %s to %s", org_path, new_path);
    add_pair(org_path, new_path);
}

const char *HOOK::query(const char *org_path) {
    std::map<std::string, std::string>::iterator iterator = IORedirectMap.find(std::string(org_path));
    if (iterator == IORedirectMap.end()) {
        return org_path;
    }
    return iterator->second.c_str();
}


const char *HOOK::restore(const char *path) {
    
}



// we hook system call
__BEGIN_DECLS

// dlopen //TODO
// dlsym //TODO
// dlclose //TODO
// execve //TODO
// fork //TODO
// vfork //TODO
/**
// int execve(const char*, char* const*, char* const*);
HOOK_DEF(int, execve, const char *filename, char *const argv[], char *const envp[]) {
    const char *redirect_path = match_redirected_path(filename);
    int ret = syscall(__NR_execve, redirect_path, argv, envp);
    
    return ret;
}
 */



// int faccessat(int dirfd, const char *pathname, int mode, int flags);
HOOK_DEF(int, faccessat, int dirfd, const char *pathname, int mode, int flags) {
    const char *redirect_path = match_redirected_path(pathname);
    int ret = syscall(__NR_faccessat, dirfd, redirect_path, mode, flags);
    
    return ret;
}


// int fchmodat(int dirfd, const char *pathname, mode_t mode, int flags);
HOOK_DEF(int, fchmodat, int dirfd, const char *pathname, mode_t mode, int flags) {
    const char *redirect_path = match_redirected_path(pathname);
    int ret = syscall(__NR_fchmodat, dirfd, redirect_path, mode, flags);
    
    return ret;
}
// int fchmod(const char *pathname, mode_t mode);
HOOK_DEF(int, fchmod, const char *pathname, mode_t mode) {
    const char *redirect_path = match_redirected_path(pathname);
    int ret = syscall(__NR_chmod, redirect_path, mode);
    
    return ret;
}


// int fstatat(int dirfd, const char *pathname, struct stat *buf, int flags);
HOOK_DEF(int, fstatat, int dirfd, const char *pathname, struct stat *buf, int flags) {
    const char *redirect_path = match_redirected_path(pathname);
    int ret = syscall(__NR_fstatat64, dirfd, redirect_path, buf, flags);
    
    return ret;
}
// int fstat(const char *pathname, struct stat *buf, int flags);
HOOK_DEF(int, fstat, const char *pathname, struct stat *buf) {
    const char *redirect_path = match_redirected_path(pathname);
    int ret = syscall(__NR_fstatat64, redirect_path, buf);
    
    return ret;
}


// int mknodat(int dirfd, const char *pathname, mode_t mode, dev_t dev);
HOOK_DEF(int, mknodat, int dirfd, const char *pathname, mode_t mode, dev_t dev) {
    const char *redirect_path = match_redirected_path(pathname);
    int ret = syscall(__NR_mknodat, dirfd, redirect_path, mode, dev);
    
    return ret;
}
// int mknod(const char *pathname, mode_t mode, dev_t dev);
HOOK_DEF(int, mknod, const char *pathname, mode_t mode, dev_t dev) {
    const char *redirect_path = match_redirected_path(pathname);
    int ret = syscall(__NR_mknod, redirect_path, mode, dev);
    
    return ret;
}


// int utimensat(int dirfd, const char *pathname, const struct timespec times[2], int flags);
HOOK_DEF(int, utimensat, int dirfd, const char *pathname, const struct timespec times[2], int flags) {
    const char *redirect_path = match_redirected_path(pathname);
    int ret = syscall(__NR_utimensat, dirfd, redirect_path, times, flags);
    
    return ret;
}


// int fchownat(int dirfd, const char *pathname, uid_t owner, gid_t group, int flags);
HOOK_DEF(int, fchownat, int dirfd, const char *pathname, uid_t owner, gid_t group, int flags) {
    const char *redirect_path = match_redirected_path(pathname);
    int ret = syscall(__NR_fchownat, dirfd, redirect_path, owner, group, flags);
    
    return ret;
}

// int chroot(const char *pathname);
HOOK_DEF(int, chroot, const char *pathname) {
    const char *redirect_path = match_redirected_path(pathname);
    int ret = syscall(__NR_chroot, redirect_path);
    
    return ret;
}


// int renameat(int olddirfd, const char *oldpath, int newdirfd, const char *newpath);
HOOK_DEF(int, renameat, int olddirfd, const char *oldpath, int newdirfd, const char *newpath) {
    const char *redirect_path_old = match_redirected_path(oldpath);
    const char *redirect_path_new = match_redirected_path(newpath);
    int ret = syscall(__NR_renameat, olddirfd, redirect_path_old, newdirfd, redirect_path_new);
    
    
    return ret;
}
// int rename(const char *oldpath, const char *newpath);
HOOK_DEF(int, rename, const char *oldpath, const char *newpath) {
    const char *redirect_path_old = match_redirected_path(oldpath);
    const char *redirect_path_new = match_redirected_path(newpath);
    int ret = syscall(__NR_rename, redirect_path_old, redirect_path_new);
    
    
    return ret;
}


// int unlinkat(int dirfd, const char *pathname, int flags);
HOOK_DEF(int, unlinkat, int dirfd, const char *pathname, int flags) {
    const char *redirect_path = match_redirected_path(pathname);
    int ret = syscall(__NR_unlinkat, dirfd, redirect_path, flags);
    
    return ret;
}
// int unlink(const char *pathname);
HOOK_DEF(int, unlink, const char *pathname) {
    const char *redirect_path = match_redirected_path(pathname);
    int ret = syscall(__NR_unlink, redirect_path);
    
    return ret;
}


// int symlinkat(const char *oldpath, int newdirfd, const char *newpath);
HOOK_DEF(int, symlinkat, const char *oldpath, int newdirfd, const char *newpath) {
    const char *redirect_path_old = match_redirected_path(oldpath);
    const char *redirect_path_new = match_redirected_path(newpath);
    int ret = syscall(__NR_symlinkat, redirect_path_old, newdirfd, redirect_path_new);
    
    
    return ret;
}
// int symlink(const char *oldpath, const char *newpath);
HOOK_DEF(int, symlink, const char *oldpath, const char *newpath) {
    const char *redirect_path_old = match_redirected_path(oldpath);
    const char *redirect_path_new = match_redirected_path(newpath);
    int ret = syscall(__NR_symlink, redirect_path_old, redirect_path_new);
    
    
    return ret;
}


// int linkat(int olddirfd, const char *oldpath, int newdirfd, const char *newpath, int flags);
HOOK_DEF(int, linkat, int olddirfd, const char *oldpath, int newdirfd, const char *newpath, int flags) {
    const char *redirect_path_old = match_redirected_path(oldpath);
    const char *redirect_path_new = match_redirected_path(newpath);
    int ret = syscall(__NR_linkat, olddirfd, redirect_path_old, newdirfd, redirect_path_new, flags);
    
    
    return ret;
}
// int link(const char *oldpath, const char *newpath);
HOOK_DEF(int, link, const char *oldpath, const char *newpath) {
    const char *redirect_path_old = match_redirected_path(oldpath);
    const char *redirect_path_new = match_redirected_path(newpath);
    int ret = syscall(__NR_link, redirect_path_old, redirect_path_new);
    
    
    return ret;
}


// int utimes(const char *filename, const struct timeval *tvp);
HOOK_DEF(int, utimes, const char *pathname, const struct timeval *tvp) {
    const char *redirect_path = match_redirected_path(pathname);
    int ret = syscall(__NR_utimes, redirect_path, tvp);
    
    return ret;
}


// int access(const char *pathname, int mode);
HOOK_DEF(int, access, const char *pathname, int mode) {
    const char *redirect_path = match_redirected_path(pathname);
    int ret = syscall(__NR_access, redirect_path, mode);
    
    return ret;
}


// int chmod(const char *path, mode_t mode);
HOOK_DEF(int, chmod, const char *pathname, mode_t mode) {
    const char *redirect_path = match_redirected_path(pathname);
    int ret = syscall(__NR_chmod, redirect_path, mode);
    
    return ret;
}


// int chown(const char *path, uid_t owner, gid_t group);
HOOK_DEF(int, chown ,const char *pathname, uid_t owner, gid_t group) {
    const char *redirect_path = match_redirected_path(pathname);
    int ret = syscall(__NR_chown, redirect_path, owner, group);
    
    return ret;
}


// int lstat(const char *path, struct stat *buf);
HOOK_DEF(int, lstat, const char *pathname, struct stat *buf) {
    char *redirect_path = const_cast<char*>(match_redirected_path(pathname));
    int ret = syscall(__NR_lstat64, redirect_path, buf);
    
    return ret;
}


// int stat(const char *path, struct stat *buf);
HOOK_DEF(int, stat, const char *pathname, struct stat *buf) {
    const char *redirect_path = match_redirected_path(pathname);
    int ret = syscall(__NR_stat64, redirect_path, buf);
    
    return ret;
}


// int mkdirat(int dirfd, const char *pathname, mode_t mode);
HOOK_DEF(int, mkdirat, int dirfd, const char *pathname, mode_t mode) {
    const char *redirect_path = match_redirected_path(pathname);
    int ret = syscall(__NR_mkdirat, dirfd, redirect_path, mode);
    
    return ret;
}
// int mkdir(const char *pathname, mode_t mode);
HOOK_DEF(int, mkdir, const char *pathname, mode_t mode) {
    const char *redirect_path = match_redirected_path(pathname);
    int ret = syscall(__NR_mkdir, redirect_path, mode);
    
    return ret;
}


// int rmdir(const char *pathname);
HOOK_DEF(int, rmdir, const char *pathname) {
    const char *redirect_path = match_redirected_path(pathname);
    int ret = syscall(__NR_rmdir, redirect_path);
    
    return ret;
}


// int readlinkat(int dirfd, const char *pathname, char *buf, size_t bufsiz);
HOOK_DEF(int, readlinkat, int dirfd, const char *pathname, char *buf, size_t bufsiz) {
    const char *redirect_path = match_redirected_path(pathname);
    int ret = syscall(__NR_readlinkat, dirfd, redirect_path, buf, bufsiz);
    
    return ret;
}
// ssize_t readlink(const char *path, char *buf, size_t bufsiz);
HOOK_DEF(ssize_t, readlink, const char *pathname, char *buf, size_t bufsiz) {
    const char *redirect_path = match_redirected_path(pathname);
    ssize_t ret = static_cast<ssize_t>(syscall(__NR_readlink, redirect_path, buf, bufsiz));
    
    return ret;
}


// int __statfs64(const char *path, size_t size, struct statfs *stat);
HOOK_DEF(int, __statfs64, const char*  pathname, size_t size, struct statfs *stat) {
    const char *redirect_path = match_redirected_path(pathname);
    int ret = syscall(__NR_statfs64, redirect_path, size, stat);
    
    return ret;
}


// int truncate(const char *path, off_t length);
HOOK_DEF(int, truncate, const char *pathname, off_t length) {
    const char *redirect_path = match_redirected_path(pathname);
    int ret = syscall(__NR_truncate, redirect_path, length);
    
    return ret;
}

// int truncate64(const char *pathname, off_t length);
HOOK_DEF(int, truncate64, const char *pathname, off_t length) {
    const char *redirect_path = match_redirected_path(pathname);
    int ret = syscall(__NR_truncate64, redirect_path, length);
    
    return ret;
}


// int chdir(const char *path);
HOOK_DEF(int, chdir, const char *pathname) {
    LOGE("chdir, org %s", pathname);
    const char *redirect_path = match_redirected_path(pathname);
    LOGE("chdir, new %s", redirect_path);
    int ret = syscall(__NR_chdir, redirect_path);
    
    return ret;
}


// int __getcwd(char *buf, size_t size);
HOOK_DEF(int, __getcwd, char *buf, size_t size) {
    const char *redirect_path = match_redirected_path(buf);
    int ret = syscall(__NR_getcwd, redirect_path, static_cast<size_t>(strlen(redirect_path)));
    
    return ret;
}


// int __openat(int fd, const char *pathname, int flags, int mode);
HOOK_DEF(int, __openat, int fd, const char *pathname, int flags, int mode) {
    const char *redirect_path = match_redirected_path(pathname);
    int ret = syscall(__NR_openat, fd, redirect_path, flags, mode);
    
    return ret;
}
// int __open(const char *pathname, int flags, int mode);
HOOK_DEF(int, __open, const char *pathname, int flags, int mode) {
    const char *redirect_path = match_redirected_path(pathname);
    int ret = syscall(__NR_open, redirect_path, flags, mode);
    
    return ret;
}

// int lchown(const char *pathname, uid_t owner, gid_t group);
HOOK_DEF(int, lchown, const char *pathname, uid_t owner, gid_t group) {
    const char *redirect_path = match_redirected_path(pathname);
    int ret = syscall(__NR_lchown, redirect_path, owner, group);
    
    return ret;
}

// int (*origin_execve)(const char *pathname, char *const argv[], char *const envp[]);
HOOK_DEF(int ,execve, const char *pathname, char *const argv[], char *const envp[]) {
    const char *redirect_path = match_redirected_path(pathname);
    int ret = org_execve(redirect_path, argv, envp);
    
    return ret;
}

__END_DECLS
// end IO hooks





void HOOK::hook(int api_level) {
    LOGI("Begin IO hooks...");

    if (true) {
        //通用型
        HOOK_IO(__getcwd);
        HOOK_IO(chdir);
        HOOK_IO(truncate);
        HOOK_IO(__statfs64);

        HOOK_IO(lchown);

        HOOK_IO(chroot);
        HOOK_IO(truncate64);
//        HOOK_IO(execve);
//        HOOK_IO(fork);
//        HOOK_IO(vfork);
    }
    if (api_level < ANDROID_L) {
        //xxx型
//        HOOK_IO(fchmod);
//        HOOK_IO(fstat);
        HOOK_IO(link);
        HOOK_IO(symlink);
        HOOK_IO(readlink);
        HOOK_IO(unlink);
        HOOK_IO(rmdir);
        HOOK_IO(rename);
        HOOK_IO(mkdir);
        HOOK_IO(stat);
        HOOK_IO(lstat);
        HOOK_IO(chown);
        HOOK_IO(chmod);
        HOOK_IO(access);
        HOOK_IO(utimes);
        HOOK_IO(__open);
        HOOK_IO(mknod);
    }

    if (api_level >= ANDROID_L) {
        ///xxxat型
        HOOK_IO(linkat);
        HOOK_IO(symlinkat);
        HOOK_IO(readlinkat);
        HOOK_IO(unlinkat);
        HOOK_IO(renameat);
        HOOK_IO(mkdirat);
        HOOK_IO(fchownat);
        HOOK_IO(utimensat);
        HOOK_IO(__openat);
        HOOK_IO(mknodat);
        HOOK_IO(fstatat);
        HOOK_IO(fchmodat);
        HOOK_IO(faccessat);
    }

    LOGI("End IO hooks SUCCESS!!!");
}
