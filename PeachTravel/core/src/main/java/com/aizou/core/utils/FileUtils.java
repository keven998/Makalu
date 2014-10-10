

package com.aizou.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 文件名	：文件操作类
 * 创建日期	：2012-10-15
 * Copyright (c) 2003-2012 北京联龙博通
 */
public class FileUtils {

	private static final String TAG = "FileUtil";

	/**
	 * 把 inputstream 复制 为 file_name
	 * @param file_name
	 * @param is
	 * @return
	 */
	public static File file_put_contents(String file_name, InputStream is) {
		File file = new File(file_name);
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
			byte buffer[] = new byte[4 * 1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				os.write(buffer, 0, len);

			}
			os.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	/**
	 * 删除此路径名表示的文件或目录。 </p>
	 * 
	 * 如果此路径名表示一个目录，则会先删除目录下的内容再将目录删除，所以该操作不是原子性的。</p>
	 * 
	 * 如果目录中还有目录，则会引发递归动作。</p>
	 * 
	 * @param filePath
	 *            要删除文件或目录的路径。
	 *            
	 * @return 当且仅当成功删除文件或目录时，返回 true；否则返回 false。
	 */
	public static boolean deleteFile(String filePath) {
		File file = new File(filePath);
		return deleteFile(file);
	}

	
	/**
	 * 删除此路径名表示的文件或目录。 </p>
	 * 
	 * 如果此路径名表示一个目录，则会先删除目录下的内容再将目录删除，所以该操作不是原子性的。</p>
	 * 
	 * 如果目录中还有目录，则会引发递归动作。</p>
	 * 
	 * @param file
	 *            要删除文件或目录的File。
	 *            
	 * @return 当且仅当成功删除文件或目录时，返回 true；否则返回 false。
	 */
	public static boolean deleteFile(File file) {
		File[] files = file.listFiles();
		if(files == null){
			return false;
		}
		for (File deleteFile : files) {
			if (deleteFile.isDirectory()) {
				// 如果是文件夹，则递归删除下面的文件后再删除该文件夹
				if (!deleteFile(deleteFile)) {
					// 如果失败则返回
					return false;
				}
			} else {
				if (!deleteFile.delete()) {
					// 如果失败则返回
					return false;
				}
			}
		}
		return file.delete();
	}
	
	
	private static ArrayList<String> mounts = new ArrayList<String>();
    private static ArrayList<String> mountsPR = new ArrayList<String>();
    private static ArrayList<String> aliases = new ArrayList<String>();
    private static ArrayList<String> aliasesPR = new ArrayList<String>();

    static {
        for (final File f : new File("/").listFiles()) {
            if (f.isDirectory()) {
                try {
                    final String cp = f.getCanonicalPath();
                    final String ap = f.getAbsolutePath();
                    if (!cp.equals(ap)) {
                        aliases.add(ap);
                        aliasesPR.add(ap + "/");
                        mounts.add(cp);
                        mountsPR.add("/");
                    }
                } catch (final IOException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
    }

    private FileUtils() {
    }

    public static final String getFileSize(final long size) {
        if (size > 1073741824) {
            return String.format("%.2f", size / 1073741824.0) + " GB";
        } else if (size > 1048576) {
            return String.format("%.2f", size / 1048576.0) + " MB";
        } else if (size > 1024) {
            return String.format("%.2f", size / 1024.0) + " KB";
        } else {
            return size + " B";
        }

    }

    public static final String getFileDate(final long time) {
        return new SimpleDateFormat("dd MMM yyyy").format(time);
    }

    public static final String getAbsolutePath(final File file) {
        return file != null ? file.getAbsolutePath() : null;
    }

    public static final String getCanonicalPath(final File file) {
        try {
            return file != null ? file.getCanonicalPath() : null;
        } catch (final IOException ex) {
            return null;
        }
    }

    public static final String invertMountPrefix(final String fileName) {
        for (int i = 0, n = Math.min(aliases.size(), mounts.size()); i < n; i++) {
            final String alias = aliases.get(i);
            final String mount = mounts.get(i);
            if (fileName.equals(alias)) {
                return mount;
            }
            if (fileName.equals(mount)) {
                return alias;
            }
        }
        for (int i = 0, n = Math.min(aliasesPR.size(), mountsPR.size()); i < n; i++) {
            final String alias = aliasesPR.get(i);
            final String mount = mountsPR.get(i);
            if (fileName.startsWith(alias)) {
                return mount + fileName.substring(alias.length());
            }
            if (fileName.startsWith(mount)) {
                return alias + fileName.substring(mount.length());
            }
        }
        return null;
    }

    public static final String getExtensionWithDot(final File file) {
        if (file == null) {
            return "";
        }
        final String name = file.getName();
        final int index = name.lastIndexOf(".");
        if (index == -1) {
            return "";
        }
        return name.substring(index);
    }

    public static final FilePath parseFilePath(final String path, final Collection<String> extensions) {
        final File file = new File(path);
        final FilePath result = new FilePath();
        result.path = LengthUtils.safeString(file.getParent());
        result.name = file.getName();

        for (final String ext : extensions) {
            final String dext = "." + ext;
            if (result.name.endsWith(dext)) {
                result.extWithDot = dext;
                result.name = result.name.substring(0, result.name.length() - ext.length() - 1);
                break;
            }
        }
        return result;
    }

    public static void copy(final File source, final File target) throws IOException {
        if (!source.exists()) {
            return;
        }
        final long length = source.length();
        final int bufsize = MathUtils.adjust((int) length, 1024, 512 * 1024);

        final byte[] buf = new byte[bufsize];
        int l = 0;
        InputStream ins = null;
        OutputStream outs = null;
        try {
            ins = new FileInputStream(source);
            outs = new FileOutputStream(target);
            for (l = ins.read(buf); l > -1; l = ins.read(buf)) {
                outs.write(buf, 0, l);
            }
        } finally {
            if (outs != null) {
                try {
                    outs.close();
                } catch (final IOException ex) {
                }
            }
            if (ins != null) {
                try {
                    ins.close();
                } catch (final IOException ex) {
                }
            }
        }
    }


    public static void copy(final InputStream source, final OutputStream target) throws IOException {
        ReadableByteChannel in = null;
        WritableByteChannel out = null;
        try {
            in = Channels.newChannel(source);
            out = Channels.newChannel(target);
            final ByteBuffer buf = ByteBuffer.allocateDirect(512 * 1024);
            while (in.read(buf) > 0) {
                buf.flip();
                out.write(buf);
                buf.flip();
            }
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (final IOException ex) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (final IOException ex) {
                }
            }
        }
    }

    public static void copy(final InputStream source, final OutputStream target, final int bufsize,
            final CopingProgress progress) throws IOException {
        ReadableByteChannel in = null;
        WritableByteChannel out = null;
        try {
            in = Channels.newChannel(source);
            out = Channels.newChannel(target);
            final ByteBuffer buf = ByteBuffer.allocateDirect(bufsize);
            long read = 0;
            while (in.read(buf) > 0) {
                buf.flip();
                read += buf.remaining();
                if (progress != null) {
                    progress.progress(read);
                }
                out.write(buf);
                buf.flip();
            }
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (final IOException ex) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (final IOException ex) {
                }
            }
        }
    }

    public static interface CopingProgress {

        void progress(long bytes);
    }

    public static final class FilePath {

        public String path;
        public String name;
        public String extWithDot;

        public File toFile() {
            return new File(path, name + LengthUtils.safeString(extWithDot));
        }
    }



}
