package org.yegpt;

import org.tensorflow.Tensor;
import org.tensorflow.ndarray.StdArrays;
import org.tensorflow.types.TInt32;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        String file = "src/main/resources/ye_rap.txt";
        YeGPT ye = new YeGPT(file);

        ye.train();

        int[] encoded = ye.encode();
        Tensor tensor = TInt32.tensorOf(StdArrays.ndCopyOf(encoded));

        int length = encoded.length;
        ByteBuffer byteBuffer = ByteBuffer.allocate(length * Integer.BYTES);

        tensor.asRawTensor().data().read(byteBuffer.array());

        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        int[] tensorData = new int[intBuffer.remaining()];
        intBuffer.get(tensorData);

//        System.out.println(Arrays.toString(tensorData));

        // instead of feeding all data at once, we will feed data as block
        int blockSize = 8;

        // Perform the slice for block
        int[] slicedData = Arrays.copyOfRange(tensorData, 0, blockSize + 1);
        System.out.println(Arrays.toString(slicedData));

        Tensor slicedTensor = TInt32.tensorOf(StdArrays.ndCopyOf(slicedData));
        tensor.close();
    }
}